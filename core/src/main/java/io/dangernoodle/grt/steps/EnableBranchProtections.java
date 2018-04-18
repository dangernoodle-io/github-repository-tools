package io.dangernoodle.grt.steps;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

import org.kohsuke.github.GHBranch;
import org.kohsuke.github.GHBranchProtectionBuilder;
import org.kohsuke.github.GHRepository;

import io.dangernoodle.grt.GithubClient;
import io.dangernoodle.grt.Repository;
import io.dangernoodle.grt.Repository.Settings.Branches;
import io.dangernoodle.grt.Repository.Settings.Branches.Protection;
import io.dangernoodle.grt.Repository.Settings.Branches.Protection.RequireReviews;
import io.dangernoodle.grt.Repository.Settings.Branches.Protection.RequiredChecks;
import io.dangernoodle.grt.Workflow.Context;
import io.dangernoodle.grt.internal.GithubWorkflow;


public class EnableBranchProtections extends GithubWorkflow.Step
{
    public EnableBranchProtections(GithubClient client)
    {
        super(client);
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
                enableProtection(ghBranch, protection, repository.getOrganization());
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
            ghBranch.disableProtection();
        }
    }

    private void enableChecks(GHBranchProtectionBuilder builder, RequiredChecks checks)
    {
        if (!checks.isEnabled())
        {
            logger.trace("checks are not required for this branch");
            return;
        }

        builder.requireBranchIsUpToDate(checks.getRequireUpToDate());

        checks.getContexts()
              .forEach(context -> {
                  logger.trace("adding required status check context [{}]", context);
                  builder.addRequiredChecks(context);
              });
    }

    private void enableProtection(GHBranch ghBranch, Protection protection, String organization) throws IOException
    {
        GHBranchProtectionBuilder builder = ghBranch.enableProtection();

        enableReviews(builder, protection.getRequireReviews());
        enableChecks(builder, protection.getRequiredChecks());
        restrictPushAccess(builder, protection, organization);

        // protection.getIncludeAdministrators();
        // protection.getRequireSignedCommits();

        builder.enable();
    }

    private void enableReviews(GHBranchProtectionBuilder builder, RequireReviews reviews)
    {
        if (!reviews.isEnabled())
        {
            logger.trace("reviews are not required for this branch");
            return;
        }

        builder.requireReviews();

        // builder.dismissStaleReviews(reviews.getDismissStaleApprovals());
        builder.requireCodeOwnReviews(reviews.getRequireCodeOwner());
    }

    private void restrictPushAccess(GHBranchProtectionBuilder builder, Protection protection, String organization)
        throws IOException
    {
        if (!protection.enablePushAccess())
        {
            logger.trace("push access is not restricted for this branch");
            return;
        }

        builder.restrictPushAccess();

        for (String team : protection.getPushTeams())
        {
            logger.trace("enabling push access for team [{}]", team);
            builder.teamPushAccess(client.getTeam(organization, team));
        }

        for (String user : protection.getPushUsers())
        {
            logger.trace("enabling push access for user [{}]", user);
            builder.userPushAccess(client.getUser(user));
        }
    }
}
