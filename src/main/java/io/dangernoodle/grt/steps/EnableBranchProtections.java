package io.dangernoodle.grt.steps;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

import org.kohsuke.github.GHBranch;
import org.kohsuke.github.GHBranchProtectionBuilder;
import org.kohsuke.github.GHRepository;

import io.dangernoodle.grt.GithubClient;
import io.dangernoodle.grt.Repository;
import io.dangernoodle.grt.Repository.Settings.AccessRestrictions;
import io.dangernoodle.grt.Repository.Settings.Branches;
import io.dangernoodle.grt.Repository.Settings.Branches.Protection;
import io.dangernoodle.grt.Repository.Settings.Branches.Protection.RequireReviews;
import io.dangernoodle.grt.Repository.Settings.Branches.Protection.RequiredChecks;
import io.dangernoodle.grt.Workflow.Context;
import io.dangernoodle.grt.extensions.StatusCheckFactory;
import io.dangernoodle.grt.internal.GithubWorkflow;


public class EnableBranchProtections extends GithubWorkflow.Step
{
    private final StatusCheckFactory factory;

    public EnableBranchProtections(GithubClient client, StatusCheckFactory factory)
    {
        super(client);
        this.factory = factory;
    }

    @Override
    public void execute(Repository repository, Context context) throws IOException
    {
        GHRepository ghRepo = context.get(GHRepository.class);
        Branches branches = repository.getSettings().getBranches();

        Collection<String> collection = new HashSet<>(branches.getOther());
        collection.add(branches.getDefault());

        for (String name : collection)
        {
            GHBranch ghBranch = ghRepo.getBranch(name);
            Protection protection = branches.getProtection(name);

            if (ghBranch == null)
            {
                logger.warn("branch [{}] does not exist in repository, cannot enable protections", name);
            }
            else if (protection.isEnabled())
            {
                enableProtection(ghBranch, protection, repository);
            }
            else
            {
                disableProtection(ghBranch);
            }
        }
    }

    private void disableProtection(GHBranch ghBranch) throws IOException
    {
        if (ghBranch.isProtected())
        {
            logger.warn("disabling branch protections for [{}]", ghBranch.getName());
            ghBranch.disableProtection();
        }
    }

    private void enableChecks(String branch, GHBranchProtectionBuilder builder, RequiredChecks checks, Repository repository)
    {
        if (!checks.isEnabled())
        {
            logger.info("branch checks are not required for [{}]", branch);
            return;
        }

        builder.requireBranchIsUpToDate(checks.getRequireUpToDate());

        factory.getRequiredStatusChecks(branch, repository)
               .forEach(context -> {
                   logger.info("adding required status check [{}] for branch [{}]", context, branch);
                   builder.addRequiredChecks(context);
               });
    }

    private void enableProtection(GHBranch ghBranch, Protection protection, Repository repository) throws IOException
    {
        String name = ghBranch.getName();
        GHBranchProtectionBuilder builder = ghBranch.enableProtection();

        enableReviews(name, builder, protection.getRequireReviews());
        enableChecks(name, builder, protection.getRequiredChecks(), repository);
        restrictPushAccess(name, builder, protection, repository.getOrganization());

        // protection.getIncludeAdministrators();
        // protection.getRequireSignedCommits();

        builder.enable();
    }

    private void enableReviews(String branch, GHBranchProtectionBuilder builder, RequireReviews reviews)
    {
        if (!reviews.isEnabled())
        {
            logger.info("reviews are not required for branch [{}]", branch);
            return;
        }

        logger.info("requiring reviews for branch [{}}", branch);
        builder.requireReviews();

        // builder.dismissStaleReviews(reviews.getDismissStaleApprovals());
        builder.requireCodeOwnReviews(reviews.getRequireCodeOwner());
    }

    private void restrictPushAccess(String branch, GHBranchProtectionBuilder builder, Protection protection, String organization)
        throws IOException
    {
        if (!protection.hasRestrictedPushAccess())
        {
            logger.info("push access is not restricted for branch [{}]", branch);
            return;
        }

        logger.info("restricting push access fpr branch [{}}", branch);
        builder.restrictPushAccess();

        AccessRestrictions pushAccess = protection.getPushAccess();

        if (pushAccess.hasTeams())
        {
            for (String team : pushAccess.getTeams())
            {
                logger.info("enabling push access on branch [{}] for team [{}]", branch, team);
                builder.teamPushAccess(client.getTeam(organization, team));
            }
        }

        if (pushAccess.hasUsers())
        {
            for (String user : pushAccess.getUsers())
            {
                logger.info("enabling push access on branch [{}] for user [{}]", branch, user);
                builder.userPushAccess(client.getUser(user));
            }
        }
    }
}
