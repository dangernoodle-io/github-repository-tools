package io.dangernoodle.grt.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
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
    private final Branches deBranches;

    private final Repository deRepository;

    private final Settings deSettings;

    private final Branches ovBranches;

    private final Repository ovRepository;

    private final Settings ovSettings;

    private final RepositoryBuilder repoBuilder;

    public RepositoryMerger(Repository defaults, Repository overrides)
    {
        this.deRepository = defaults;
        this.ovRepository = overrides;

        this.deSettings = deRepository.getSettings();
        this.ovSettings = ovRepository.getSettings();

        this.deBranches = deSettings.getBranches();
        this.ovBranches = ovSettings.getBranches();

        this.repoBuilder = new RepositoryBuilder();
    }

    public Repository merge() throws IllegalStateException
    {
        repoBuilder.setName(ovRepository.getName());

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

    private void addBranchProtection(String branch, Protection protection)
    {
        repoBuilder.requireSignedCommits(branch, merge(protection.getRequireSignedCommits(), false))
                   .enforceForAdminstrators(branch, merge(protection.getIncludeAdministrators(), false));

        if (protection.hasRequireReviews())
        {
            addRequireReviews(branch, protection.getRequireReviews());
        }

        if (protection.hasRequiredChecks())
        {
            addRequireChecks(branch, protection.getRequiredChecks());
        }

        if (protection.enableRestrictedPushAccess())
        {
            repoBuilder.restrictPushAccess(branch);

            protection.getPushTeams()
                      .forEach(team -> repoBuilder.addTeamPushAccess(branch, team));

            protection.getPushUsers()
                      .forEach(user -> repoBuilder.addUserPushAccess(branch, user));
        }
    }

    private void addRequireChecks(String branch, RequiredChecks requireChecks)
    {
        repoBuilder.requireBranchUpToDate(branch, requireChecks.getRequireUpToDate());
        if (requireChecks.hasContexts())
        {
            requireChecks.getContexts()
                         .forEach(context -> repoBuilder.addRequiredContext(branch, context));
        }
    }

    private void addRequireReviews(String branch, RequireReviews requireReviews)
    {
        repoBuilder.requireReviews(branch)
                   .requiredReviewers(branch, merge(requireReviews.getRequiredReviewers(), 1))
                   .dismissStaleApprovals(branch, merge(requireReviews.getDismissStaleApprovals(), false))
                   .requireCodeOwnerReview(branch, merge(requireReviews.getRequireCodeOwner(), false));

        if (requireReviews.hasDismissalTeams())
        {
            requireReviews.getDismissalTeams()
                          .forEach(team -> repoBuilder.addTeamReviewDismisser(branch, team));
        }

        if (requireReviews.hasDismissalUsers())
        {
            requireReviews.getDismissalUsers()
                          .forEach(user -> repoBuilder.addUserReviewDismisser(branch, user));
        }
    }

    private String getPrimaryBranch()
    {
        String branch = ovBranches.getDefault();
        if (branch == null)
        {
            branch = deBranches.getDefault();
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

    private void mergeAutoInitialize()
    {
        repoBuilder.setInitialize(merge(ovSettings.autoInitialize(), deSettings.autoInitialize()));
    }

    private void mergeBranches()
    {
        String primary = getPrimaryBranch();
        repoBuilder.setPrimaryBranch(primary);

        Collection<String> branches = new ArrayList<>(merge(deBranches.getOther(), ovBranches.getOther()));
        repoBuilder.addOtherBranches(branches);

        branches.add(primary);
        branches.forEach(this::mergeProtections);
    }

    private void mergeProtections(String branch)
    {
        Protection deProtection = deBranches.getProtection(branch);
        Protection ovProtection = ovBranches.getProtection(branch);

        if (ovBranches.hasProtection(branch))
        {
            if (ovProtection.isEnabled())
            {
                addBranchProtection(branch, ovProtection);
            }
        }
        else if (deBranches.hasProtection(branch))
        {
            if (deProtection.isEnabled())
            {
                addBranchProtection(branch, deProtection);
            }
        }
    }

    private void mergeColaborators()
    {
        merge(ovSettings.getCollaborators(), deSettings.getCollaborators(), new Callback<Permission>()
        {
            @Override
            public void add()
            {
                repoBuilder.addCollaborators();
            }

            @Override
            public void add(String key, Permission value)
            {
                repoBuilder.addCollaborator(key, value);
            }
        });
    }

    private void mergeLabels()
    {
        merge(ovSettings.getLabels(), deSettings.getLabels(), new Callback<Color>()
        {
            @Override
            public void add()
            {
                repoBuilder.addLabels();
            }

            @Override
            public void add(String key, Color value)
            {
                repoBuilder.addLabel(key, value);
            }
        });
    }

    private void mergeOrganization() throws IllegalStateException
    {
        String organization = ovRepository.getOrganization();
        if (organization == null)
        {
            organization = deRepository.getOrganization();
            if (organization == null)
            {
                throw new IllegalStateException("organization must be specified");
            }
        }

        repoBuilder.setOrganization(organization);
    }

    private void mergePlugins()
    {
        Map<String, Object> dePlugins = Optional.ofNullable(deRepository.getPlugins())
                                                .orElse(Collections.emptyMap());

        Map<String, Object> ovPlugins = Optional.ofNullable(ovRepository.getPlugins())
                                                .orElse(Collections.emptyMap());

        Map<String, Object> merged = new HashMap<>(dePlugins);
        merged.putAll(ovPlugins);

        merged.forEach(repoBuilder::addPlugin);
    }

    private void mergePrivate()
    {
        repoBuilder.setPrivate(merge(ovSettings.isPrivate(), deSettings.isPrivate()));
    }

    private void mergeTeams()
    {
        merge(ovSettings.getTeams(), deSettings.getTeams(), new Callback<Permission>()
        {
            @Override
            public void add()
            {
                repoBuilder.addTeams();
            }

            @Override
            public void add(String key, Permission value)
            {
                repoBuilder.addTeam(key, value);
            }
        });
    }

    private interface Callback<V>
    {
        void add();

        void add(String key, V value);
    }

    private void mergeWorkflow()
    {
        merge(ovRepository.getWorkflow(), deRepository.getWorkflow()).forEach(repoBuilder::addWorkflow);
    }
}
