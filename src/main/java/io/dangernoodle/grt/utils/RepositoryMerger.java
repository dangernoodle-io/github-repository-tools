package io.dangernoodle.grt.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import io.dangernoodle.grt.Repository;
import io.dangernoodle.grt.Repository.Settings;
import io.dangernoodle.grt.Repository.Settings.AccessRestrictions;
import io.dangernoodle.grt.Repository.Settings.Branches;
import io.dangernoodle.grt.Repository.Settings.Branches.Protection;
import io.dangernoodle.grt.Repository.Settings.Branches.Protection.RequireReviews;
import io.dangernoodle.grt.Repository.Settings.Branches.Protection.RequiredChecks;
import io.dangernoodle.grt.Repository.Settings.Color;
import io.dangernoodle.grt.Repository.Settings.Permission;


public class RepositoryMerger
{
    private JsonTransformer transformer;

    public RepositoryMerger(JsonTransformer transformer)
    {
        this.transformer = transformer;
    }

    public Repository merge(Repository repository) throws IllegalStateException
    {
        RepositoryBuilder builder = createBuilder();
        return merge(repository, builder.build(), builder);
    }

    public Repository merge(Repository overrides, Repository defaults) throws IllegalStateException
    {
        return merge(overrides, defaults, createBuilder());
    }

    // visible for testing
    RepositoryBuilder createBuilder()
    {
        return new RepositoryBuilder(transformer);
    }

    private void addBranchProtection(String branch, Protection protection, RepositoryBuilder builder)
    {
        builder.requireSignedCommits(branch, merge(protection.getRequireSignedCommits(), false))
               .enforceForAdminstrators(branch, merge(protection.getIncludeAdministrators(), false));

        if (protection.hasRequireReviews())
        {
            addRequireReviews(branch, protection.getRequireReviews(), builder);
        }

        if (protection.hasRequiredChecks())
        {
            addRequireChecks(branch, protection.getRequiredChecks(), builder);
        }

        if (protection.hasRestrictedPushAccess())
        {
            addRestrictedPushAccess(branch, protection, builder);
        }
    }

    private void addRequireChecks(String branch, RequiredChecks requireChecks, RepositoryBuilder builder)
    {
        builder.requireBranchUpToDate(branch, requireChecks.getRequireUpToDate());
        if (requireChecks.hasContexts())
        {
            requireChecks.getContexts()
                         .forEach(context -> builder.addRequiredContext(branch, context));
        }
    }

    private void addRequireReviews(String branch, RequireReviews requireReviews, RepositoryBuilder builder)
    {
        builder.requireReviews(branch)
                   .requiredReviewers(branch, merge(requireReviews.getRequiredReviewers(), 1))
                   .dismissStaleApprovals(branch, merge(requireReviews.getDismissStaleApprovals(), false))
                   .requireCodeOwnerReview(branch, merge(requireReviews.getRequireCodeOwner(), false));

        if (requireReviews.hasDismissalRestrictions())
        {
            AccessRestrictions restrictions = requireReviews.getDismissalRestrictions();
            if (restrictions.hasTeams())
            {
                restrictions.getTeams()
                            .forEach(team -> builder.addTeamReviewDismisser(branch, team));
            }

            if (restrictions.hasUsers())
            {
                restrictions.getUsers()
                            .forEach(user -> builder.addUserReviewDismisser(branch, user));
            }
        }
    }

    private void addRestrictedPushAccess(String branch, Protection protection, RepositoryBuilder builder)
    {
        builder.restrictPushAccess(branch);

        AccessRestrictions restrictions = protection.getPushAccess();
        if (restrictions.hasTeams())
        {
            restrictions.getTeams()
                        .forEach(team -> builder.addTeamPushAccess(branch, team));
        }

        if (restrictions.hasUsers())
        {
            restrictions.getUsers()
                        .forEach(user -> builder.addUserPushAccess(branch, user));
        }
    }

    private String getPrimaryBranch(Branches overrides, Branches defaults)
    {
        String branch = overrides.getDefault();
        if (branch == null)
        {
            branch = defaults.getDefault();
            if (branch == null)
            {
                branch = "master";
            }
        }

        return branch;
    }

    private boolean merge(Boolean override, Boolean defaults)
    {
        return merge(override, defaults, false);
    }

    private Collection<String> merge(Collection<String> override, Collection<String> defaults)
    {
        return merge(override, defaults, Collections.emptyList());
    }

    private int merge(Integer override, Integer defaults)
    {
        return merge(override, defaults, 1);
    }

    private <V> void merge(Map<String, V> overrides, Map<String, V> defaults, Callback<V> callback)
    {
        Map<String, V> merged = merge(overrides, defaults, Collections.emptyMap());

        if (merged.isEmpty())
        {
            callback.add();
        }
        else
        {
            merged.forEach(callback::add);
        }
    }

    private Repository merge(Repository overrides, Repository defaults, RepositoryBuilder builder) throws IllegalStateException
    {
        // there can be only one
        builder.setName(overrides.getName());
        builder.setOrganization(mergeOrganization(overrides, defaults));

        mergeSettings(overrides.getSettings(), defaults.getSettings(), builder);

        mergePlugins(overrides, defaults, builder);
        mergeWorkflow(overrides, defaults, builder);

        return builder.build();
    }

    private <T> T merge(T override, T defaults, T dflt)
    {
        if (override != null)
        {
            return override;
        }

        if (defaults != null)
        {
            return defaults;
        }

        return dflt;
    }

    private void mergeBranches(Branches overrides, Branches defaults, RepositoryBuilder builder)
    {
        String primary = getPrimaryBranch(overrides, defaults);
        builder.setPrimaryBranch(primary);

        Collection<String> branches = new ArrayList<>(merge(overrides.getOther(), defaults.getOther()));
        builder.addOtherBranches(branches);

        branches.add(primary);
        branches.forEach(branch -> mergeProtections(overrides, defaults, branch, builder));
    }

    private String mergeOrganization(Repository overrides, Repository defaults) throws IllegalStateException
    {
        String organization = overrides.getOrganization();
        if (organization == null)
        {
            organization = defaults.getOrganization();
            if (organization == null)
            {
                throw new IllegalStateException("organization must be specified");
            }
        }

        return organization;
    }

    private void mergePlugins(Repository overrides, Repository defaults, RepositoryBuilder builder)
    {
        Map<String, Object> dePlugins = Optional.ofNullable(overrides.getPlugins())
                                                .orElse(Collections.emptyMap());

        Map<String, Object> ovPlugins = Optional.ofNullable(defaults.getPlugins())
                                                .orElse(Collections.emptyMap());

        Map<String, Object> merged = new HashMap<>(dePlugins);
        merged.putAll(ovPlugins);

        merged.forEach(builder::addPlugin);
    }

    private void mergeProtections(Branches overrides, Branches defaults, String branch, RepositoryBuilder builder)
    {
        Protection deProtection = defaults.getProtection(branch);
        Protection ovProtection = overrides.getProtection(branch);

        if (overrides.hasProtection(branch))
        {
            if (ovProtection.isEnabled())
            {
                addBranchProtection(branch, ovProtection, builder);
            }
        }
        else if (defaults.hasProtection(branch))
        {
            if (deProtection.isEnabled())
            {
                addBranchProtection(branch, deProtection, builder);
            }
        }
    }

    private void mergeSettings(Settings overrides, Settings defaults, RepositoryBuilder builder)
    {
        builder.setInitialize(merge(overrides.autoInitialize(), defaults.autoInitialize()));
        builder.setPrivate(merge(overrides.isPrivate(), defaults.isPrivate()));

        merge(overrides.getLabels(), defaults.getLabels(), new Callback<Color>()
        {
            @Override
            public void add()
            {
                builder.addLabels();
            }

            @Override
            public void add(String key, Color value)
            {
                builder.addLabel(key, value);
            }
        });

        merge(overrides.getCollaborators(), defaults.getCollaborators(), new Callback<Permission>()
        {
            @Override
            public void add()
            {
                builder.addCollaborators();
            }

            @Override
            public void add(String key, Permission value)
            {
                builder.addCollaborator(key, value);
            }
        });

        merge(overrides.getTeams(), defaults.getTeams(), new Callback<Permission>()
        {
            @Override
            public void add()
            {
                builder.addTeams();
            }

            @Override
            public void add(String key, Permission value)
            {
                builder.addTeam(key, value);
            }
        });

        mergeBranches(overrides.getBranches(), defaults.getBranches(), builder);
    }

    private void mergeWorkflow(Repository overrides, Repository defaults, RepositoryBuilder builder)
    {
        merge(overrides.getWorkflow(), defaults.getWorkflow()).forEach(builder::addWorkflow);
    }

    private interface Callback<V>
    {
        void add();

        void add(String key, V value);
    }
}
