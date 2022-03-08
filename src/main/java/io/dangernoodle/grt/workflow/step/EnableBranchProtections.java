package io.dangernoodle.grt.workflow.step;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

import org.kohsuke.github.GHBranch;
import org.kohsuke.github.GHBranchProtection;
import org.kohsuke.github.GHBranchProtectionBuilder;
import org.kohsuke.github.GHRepository;

import io.dangernoodle.grt.Repository;
import io.dangernoodle.grt.Repository.Settings.AccessRestrictions;
import io.dangernoodle.grt.Repository.Settings.Branches;
import io.dangernoodle.grt.Repository.Settings.Branches.Protection;
import io.dangernoodle.grt.Repository.Settings.Branches.Protection.RequireReviews;
import io.dangernoodle.grt.Repository.Settings.Branches.Protection.RequiredChecks;
import io.dangernoodle.grt.StatusCheck;
import io.dangernoodle.grt.Workflow.Context;
import io.dangernoodle.grt.Workflow.Status;
import io.dangernoodle.grt.util.GithubClient;


public class EnableBranchProtections extends AbstractGithubStep
{
    private final StatusCheck statusCheck;

    public EnableBranchProtections(GithubClient client)
    {
        this(client, StatusCheck.NULL);
    }

    public EnableBranchProtections(GithubClient client, StatusCheck statusCheck)
    {
        super(client);
        this.statusCheck = statusCheck;
    }

    @Override
    public Status execute(Repository repository, Context context) throws IOException
    {
        GHRepository ghRepo = context.getGHRepository();
        Branches branches = repository.getSettings().getBranches();

        Collection<String> collection = getBranchNamesToProtect(branches);

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

        return Status.CONTINUE;
    }

    protected Collection<String> getBranchNamesToProtect(Branches branches)
    {
        Collection<String> collection = new HashSet<>(branches.getOther());
        collection.add(branches.getDefault());

        return collection;
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

        statusCheck.getRequiredChecks(branch, repository)
                   .forEach(context -> {
                       logger.info("adding required status check [{}] for branch [{}]", context, branch);
                       builder.addRequiredChecks(context);
                   });
    }

    private void enableCommitSigning(GHBranchProtection ghProtection, boolean signCommits) throws IOException
    {
        boolean ghSign = ghProtection.getRequiredSignatures();

        if (signCommits && !ghSign)
        {
            ghProtection.enabledSignedCommits();
        }
        else if (ghSign && !signCommits)
        {
            ghProtection.disableSignedCommits();
        }
    }

    private void enableProtection(GHBranch ghBranch, Protection protection, Repository repository) throws IOException
    {
        String name = ghBranch.getName();
        GHBranchProtectionBuilder builder = ghBranch.enableProtection();

        enableReviews(name, builder, protection.getRequireReviews());
        enableChecks(name, builder, protection.getRequiredChecks(), repository);
        restrictPushAccess(name, builder, protection, repository.getOrganization());

        if (protection.getIncludeAdministrators())
        {
            builder.includeAdmins();
        }

        GHBranchProtection ghProtection = builder.enable();
        enableCommitSigning(ghProtection, protection.getRequireSignedCommits());
    }

    private void enableReviews(String branch, GHBranchProtectionBuilder builder, RequireReviews reviews)
    {
        if (!reviews.isEnabled())
        {
            logger.info("reviews are not required for branch [{}]", branch);
            return;
        }

        int reviewers = reviews.getRequiredReviewers();
        boolean reqOwners = reviews.getRequireCodeOwner();
        boolean dismissStale = reviews.getDismissStaleApprovals();

        logger.info("requiring reviews for branch [{}] - codeOwners [{}] - reviewers [{}] - dismissStale [{}]",
                branch, reqOwners, reviewers, dismissStale);

        builder.requireReviews();
        builder.requiredReviewers(reviewers);
        builder.requireCodeOwnReviews(reqOwners);
        builder.dismissStaleReviews(dismissStale);
    }

    private void restrictPushAccess(String branch, GHBranchProtectionBuilder builder, Protection protection, String organization)
        throws IOException
    {
        if (!protection.hasRestrictedPushAccess())
        {
            logger.info("push access is not restricted for branch [{}]", branch);
            return;
        }

        logger.info("restricting push access for branch [{}]", branch);
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
