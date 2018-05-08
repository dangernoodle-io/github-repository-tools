package io.dangernoodle.grt.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

import io.dangernoodle.grt.Repository;
import io.dangernoodle.grt.Repository.Color;
import io.dangernoodle.grt.Repository.Permission;
import io.dangernoodle.grt.Repository.Settings;
import io.dangernoodle.grt.Repository.Settings.Branches;
import io.dangernoodle.grt.Repository.Settings.Branches.Protection;
import io.dangernoodle.grt.Repository.Settings.Branches.Protection.RequireReviews;
import io.dangernoodle.grt.Repository.Settings.Branches.Protection.RequiredChecks;


public class RepositoryMerger
{
    private final Branches dBranches;

    private final Repository dRepository;

    private final Settings dSettings;

    private final Branches oBranches;

    private final Repository oRepository;

    private final Settings oSettings;

    private final RepositoryBuilder repoBuilder;

    public RepositoryMerger(Repository defaults, Repository overrides)
    {
        this.dRepository = defaults;
        this.oRepository = overrides;

        this.dSettings = dRepository.getSettings();
        this.oSettings = oRepository.getSettings();

        this.dBranches = dSettings.getBranches();
        this.oBranches = oSettings.getBranches();

        this.repoBuilder = new RepositoryBuilder();
    }

    public Repository merge() throws IllegalStateException
    {
        repoBuilder.setName(oRepository.getName());

        mergeOrganization();
        mergeAutoInitialize();
        mergePrivate();
        mergeLabels();
        mergeTeams();
        mergeColaborators();
        mergeBranches();

        mergePlugins();
        mergeWorkflow();

        return repoBuilder.build();
    }

    private void addBranchProtection(String branch, ProtectionDelegate protection)
    {
        repoBuilder.requireSignedCommits(branch, protection.requireSignedCommits())
                   .enforceForAdminstrators(branch, protection.enforceForAdminstrators());

        if (protection.requireReviews())
        {
            repoBuilder.requireReviews(branch)
                       .requiredReviewers(branch, protection.getRequiredReviewers())
                       .dismissStaleApprovals(branch, protection.getDismissStaleApprovals())
                       .requireCodeOwnerReview(branch, protection.getRequireCodeOwner());

            protection.getRetrictDismissalTeams()
                      .forEach(team -> repoBuilder.addTeamReviewDismisser(branch, team));

            protection.getRetrictDismissalUsers()
                      .forEach(user -> repoBuilder.addUserReviewDismisser(branch, user));
        }

        if (protection.requireStatusChecks())
        {
            repoBuilder.requireBranchUpToDate(branch, protection.getRequireUpToDate());
            protection.getRequiredContexts()
                      .forEach(context -> repoBuilder.addRequiredContext(branch, context));
        }

        if (protection.enablePushAccess())
        {
            repoBuilder.restrictPushAccess(branch);

            protection.getTeamPushAccess()
                      .forEach(team -> repoBuilder.addTeamPushAccess(branch, team));

            protection.getUserPushAccess()
                      .forEach(user -> repoBuilder.addUserPushAccess(branch, user));
        }
    }

    private String getPrimaryBranch()
    {
        String branch = oSettings.getBranches().getDefault();
        if (branch == null)
        {
            branch = dSettings.getBranches().getDefault();
            if (branch == null)
            {
                branch = "master";
            }
        }

        return branch;
    }

    private <V> Collection<V> join(Collection<V> defaults, Collection<V> overrides, boolean merge)
    {
        HashSet<V> merged = new HashSet<>(defaults.size() + overrides.size());

        if (merge)
        {
            merged.addAll(defaults);
        }

        merged.addAll(overrides);

        return merged;
    }

    private <V> Map<String, V> join(Map<String, V> defaults, Map<String, V> overrides, boolean merge)
    {
        Map<String, V> merged = new HashMap<>();

        if (merge)
        {
            merged.putAll(defaults);
        }

        merged.putAll(overrides);

        return merged;
    }

    private void mergeAutoInitialize()
    {
        repoBuilder.setInitialize(oSettings.autoInitialize() ? true : dSettings.autoInitialize());
    }

    private void mergeWorkflow()
    {
        Collection<String> workflow = oRepository.getWorkflow();
        if (workflow == null)
        {
            workflow = dRepository.getWorkflow();
        }

        Optional.ofNullable(workflow)
                .orElse(Collections.emptyList())
                .forEach(repoBuilder::addWorkflow);
    }

    private void mergeBranches()
    {
        String primary = getPrimaryBranch();
        repoBuilder.setPrimaryBranch(primary);

        Collection<String> branches = join(dBranches.getOther(), oBranches.getOther(), true);

        branches.forEach(repoBuilder::addOtherBranch);
        branches.add(primary);

        branches.forEach(branch -> {
            Protection dProtection = dBranches.getProtection(branch);
            Protection oProtection = oBranches.getProtection(branch);

            addBranchProtection(branch, new ProtectionDelegate(dProtection, oProtection));
        });
    }

    private void mergeColaborators()
    {
        Map<String, Permission> users = join(dSettings.getCollaborators(), oSettings.getCollaborators(), true);
        users.forEach((k, v) -> repoBuilder.addCollaborator(k, v));
    }

    private void mergeLabels()
    {
        Map<String, Color> labels = join(dSettings.getLabels(), oSettings.getLabels(), true);
        labels.forEach((k, v) -> repoBuilder.addLabel(k, v));
    }

    private void mergeOrganization() throws IllegalStateException
    {
        String organization = oRepository.getOrganization();
        if (organization == null)
        {
            organization = dRepository.getOrganization();
            if (organization == null)
            {
                throw new IllegalStateException("organization must be specified");
            }
        }

        repoBuilder.setOrganization(organization);
    }

    private void mergePlugins()
    {
        Map<String, String> plugins = join(dRepository.getPlugins(), oRepository.getPlugins(), true);
        plugins.forEach((k, v) -> repoBuilder.addPlugin(k, v.toString()));
    }

    private void mergePrivate()
    {
        repoBuilder.setPrivate(oSettings.isPrivate() ? true : dSettings.isPrivate());
    }

    private void mergeTeams()
    {
        Map<String, Permission> teams = join(dSettings.getTeams(), oSettings.getTeams(), true);
        teams.forEach((k, v) -> repoBuilder.addTeam(k, v));
    }

    private class ProtectionDelegate
    {
        private final Protection dProtection;

        private final RequiredChecks dRequiredChecks;

        private final RequireReviews dRequireReviews;

        private final Protection oProtection;

        private final RequiredChecks oRequiredChecks;

        private final RequireReviews oRequireReviews;

        ProtectionDelegate(Protection dProtection, Protection oProtection)
        {
            this.dProtection = dProtection;
            this.oProtection = oProtection;

            this.dRequireReviews = dProtection.getRequireReviews();
            this.oRequireReviews = oProtection.getRequireReviews();

            this.dRequiredChecks = dProtection.getRequiredChecks();
            this.oRequiredChecks = oProtection.getRequiredChecks();
        }

        boolean enablePushAccess()
        {
            boolean override = false;

            if (override)
            {
                return false;
            }

            return oProtection.enablePushAccess() || dProtection.enablePushAccess();
        }

        boolean enforceForAdminstrators()
        {
            return oProtection.getIncludeAdministrators() ? true : dProtection.getIncludeAdministrators();
        }

        boolean getDismissStaleApprovals()
        {
            return oRequireReviews.getDismissStaleApprovals() ? true : dRequireReviews.getDismissStaleApprovals();
        }

        boolean getRequireCodeOwner()
        {
            return oRequireReviews.getRequireCodeOwner() ? true : dRequireReviews.getRequireCodeOwner();
        }

        Collection<String> getRequiredContexts()
        {
            Collection<String> contexts = new ArrayList<>(dRequiredChecks.getContexts());
            contexts.addAll(oRequiredChecks.getContexts());

            return contexts;
        }

        int getRequiredReviewers()
        {
            int reviewers = oRequireReviews.getRequiredReviewers();
            return reviewers > 1 ? reviewers : dRequireReviews.getRequiredReviewers();
        }

        boolean getRequireUpToDate()
        {
            return oRequiredChecks.getRequireUpToDate() ? true : dRequiredChecks.getRequireUpToDate();
        }

        Collection<String> getRetrictDismissalTeams()
        {
            return join(dRequireReviews.getDismissalTeams(), oRequireReviews.getDismissalTeams(), true);
        }

        Collection<String> getRetrictDismissalUsers()
        {
            return join(dRequireReviews.getDismissalUsers(), oRequireReviews.getDismissalUsers(), true);
        }

        Collection<String> getTeamPushAccess()
        {
            return join(dProtection.getPushTeams(), oProtection.getPushTeams(), true);
        }

        Collection<String> getUserPushAccess()
        {
            return join(dProtection.getPushUsers(), oProtection.getPushUsers(), true);
        }

        boolean requireReviews()
        {
            boolean override = false;

            if (override)
            {
                return false;
            }

            return oRequireReviews != null || dRequireReviews != null;
        }

        boolean requireSignedCommits()
        {
            return oProtection.getRequireSignedCommits() ? true : dProtection.getRequireSignedCommits();
        }

        boolean requireStatusChecks()
        {
            boolean override = false;

            if (override)
            {
                return false;
            }

            return oRequiredChecks != null || dRequiredChecks != null;
        }
    }
}
