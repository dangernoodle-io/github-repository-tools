package io.dangernoodle;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.util.Collection;
import java.util.Map;

import io.dangernoodle.grt.Repository;
import io.dangernoodle.grt.Repository.Color;
import io.dangernoodle.grt.Repository.Permission;
import io.dangernoodle.grt.Repository.Settings.Branches.Protection;


public final class TestAsserts
{
    public static void verifyBranchesIsNotNull(Repository actual)
    {
        assertThat(actual.getSettings(), notNullValue());
        assertThat(actual.getSettings().getBranches(), notNullValue());
    }

    public static void verifyBranchProtectionEnabledOnly(Repository actual, String branch)
    {
        Protection protection = getProtection(actual, branch);
        assertThat(protection.isEnabled(), equalTo(true));
        
        verifyRequireReviewsDisabled(actual, branch);
        verifyRequireReviewsDisabled(actual, branch);
        verifyPushAccessIsUnrestricted(actual, branch);
    }

    public static void verifyBranchProtectionIsDisabled(Repository actual, String branch)
    {
        Protection protection = getProtection(actual, branch);
        assertThat(protection.isEnabled(), equalTo(false));
    }

    public static void verifyBranchProtectionsAreAllEnabled(Repository actual, Repository expected, String branch)
    {
        verifyRequireSignedCommitsEnabled(actual, branch);
        verifyEnforeForAdministratorsEnabled(actual, branch);
        verifyRequireReviewsEnabled(actual, branch);
        verifyRequiredStatusChecksEnabled(actual, branch);
        verifyPushAccessIsRestricted(actual, expected, branch);
    }

    public static void verifyBranchProtectionsAreAllNull(Repository actual, String branch)
    {
        verifyRequireSignedCommitsIsNull(actual, branch);
        verifyEnforeForAdministratorsIsNull(actual, branch);
        verifyRequireReviewsIsNull(actual, branch);
        verifyRequiredStatusChecksIsNull(actual, branch);
        // odd ball b/c 'push access' isn't an object
        assertThat(getProtection(actual, branch).enableRestrictedPushAccess(), equalTo(true));
    }

    public static void verifyCollaborators(Repository actual, Map<String, Permission> expected)
    {
        verifyPermissions(actual.getSettings().getCollaborators(), expected);
    }

    public static void verifyCollaborators(Repository actual, Repository expected)
    {
        verifyCollaborators(actual, expected.getSettings().getCollaborators());
    }

    public static void verifyCollaboratorsAreEmpty(Repository actual)
    {
        verifyPermissionsAreEmpty(actual.getSettings().getCollaborators());
    }

    public static void verifyEnforeForAdministratorsDisabled(Repository actual, String branch)
    {
        assertThat(getProtection(actual, branch).getIncludeAdministrators(), equalTo(false));
    }

    public static void verifyEnforeForAdministratorsEnabled(Repository actual, String branch)
    {
        assertThat(getProtection(actual, branch).getIncludeAdministrators(), equalTo(true));
    }

    public static void verifyEnforeForAdministratorsIsNull(Repository actual, String branch)
    {
        assertThat(getProtection(actual, branch).getIncludeAdministrators(), nullValue());
    }

    public static void verifyLabels(Map<String, Color> actual, Map<String, Color> expected)
    {
        assertThat(actual, notNullValue());
        assertThat(actual.size(), equalTo(1));
        assertThat(actual.keySet(), equalTo(expected.keySet()));
        assertThat(actual.values(), contains(expected.values().toArray()));
    }

    public static void verifyLabels(Repository actual, Map<String, Color> expected)
    {
        verifyLabels(actual.getSettings().getLabels(), expected);
    }

    public static void verifyLabelsAreEmpty(Map<String, Color> actual)
    {
        assertThat(actual, notNullValue());
        assertThat(actual.isEmpty(), equalTo(true));
    }
    
    public static void verifyNoStatusChecksAreRequired(Repository repository, String branch)
    {
        assertThat(getProtection(repository, branch).getRequiredChecks().hasContexts(), equalTo(false));
    }

    public static void verifyOrganization(Repository actual, Repository expected)
    {
        assertThat(actual.getOrganization(), equalTo(expected.getOrganization()));
    }

    public static void verifyOtherBranches(Repository actual, String branch, Collection<String> other)
    {
        assertThat(actual.getSettings().getBranches().getOther().isEmpty(), equalTo(false));
        assertThat(actual.getSettings().getBranches().getOther().size(), equalTo(other.size()));
        assertThat(actual.getSettings().getBranches().getOther(), contains(other.toArray()));
    }

    public static void verifyPrimaryBranch(Repository actual, String expectedBranch)
    {
        assertThat(actual.getSettings().getBranches().getDefault(), equalTo(expectedBranch));
    }

    public static void verifyPushAccessIsRestricted(Repository actual, Repository expected, String branch)
    {
        Protection aProtection = getProtection(actual, branch);
        Protection eProtection = getProtection(expected, branch);

        assertThat(aProtection.enableRestrictedPushAccess(), equalTo(true));
        assertThat(aProtection.getPushTeams(), contains(eProtection.getPushTeams().toArray()));
        assertThat(aProtection.getPushUsers(), contains(eProtection.getPushUsers().toArray()));
    }

    public static void verifyPushAccessIsRestricted(Repository actual, String branch)
    {
        assertThat(getProtection(actual, branch).enableRestrictedPushAccess(), equalTo(true));
    }

    public static void verifyPushAccessIsUnrestricted(Repository repository, String branch)
    {
        Protection protection = getProtection(repository, branch);
        assertThat(protection.enableRestrictedPushAccess(), equalTo(false));
    }

    public static void verifyPushAccessTeams(Repository actual, String branch, Collection<String> teams)
    {
        assertThat(getProtection(actual, branch).getPushTeams(), notNullValue());
        assertThat(getProtection(actual, branch).getPushTeams().size(), equalTo(teams.size()));
        assertThat(getProtection(actual, branch).getPushTeams(), contains(teams.toArray()));
    }

    public static void verifyPushAccessUsers(Repository actual, String branch, Collection<String> users)
    {
        assertThat(getProtection(actual, branch).getPushUsers(), notNullValue());
        assertThat(getProtection(actual, branch).getPushUsers().size(), equalTo(users.size()));
        assertThat(getProtection(actual, branch).getPushUsers(), contains(users.toArray()));
    }

    public static void verifyRepositoryInitialized(Repository actual)
    {
        assertThat(actual.getSettings().autoInitialize(), equalTo(true));
    }

    public static void verifyRepositoryIsPrivate(Repository actual)
    {
        assertThat(actual.getSettings().isPrivate(), equalTo(true));
    }

    public static void verifyRepositoryName(Repository actual, Repository expected)
    {
        verifyRepositoryName(actual, expected.getName());
    }

    public static void verifyRepositoryName(Repository actual, String name)
    {
        assertThat(actual.getName(), equalTo(name));
    }

    public static void verifyRepositoryNotInitialized(Repository actual)
    {
        assertThat(actual.getSettings().autoInitialize(), equalTo(false));
    }

    public static void verifyRepositoryOrganization(Repository actual, Repository expected)
    {
        verifyRepositoryOrganization(actual, expected.getOrganization());
    }

    public static void verifyRepositoryOrganization(Repository actual, String organization)
    {
        assertThat(actual.getOrganization(), equalTo(organization));
    }

    public static void verifyRequiredStatusChecksDisabled(Repository repository, String branch)
    {
        Protection protection = getProtection(repository, branch);

        assertThat(protection.getRequiredChecks(), notNullValue());
        assertThat(protection.getRequiredChecks().isEnabled(), equalTo(false));
    }

    public static void verifyRequiredStatusChecksEnabled(Repository repository, String branch)
    {
        Protection protection = getProtection(repository, branch);

        assertThat(protection.getRequiredChecks(), notNullValue());
        assertThat(protection.getRequiredChecks().isEnabled(), equalTo(true));
        assertThat(protection.getRequiredChecks().getRequireUpToDate(), equalTo(true));
        assertThat(protection.getRequiredChecks().getContexts(), contains(repository.getName()));
    }
    
    public static void verifyRequiredStatusChecksIsNull(Repository actual, String branch)
    {
        assertThat(getProtection(actual, branch).getRequiredChecks(), notNullValue());
    }
    
    public static void verifyRequireReviewsDisabled(Repository repository, String branch)
    {
        Protection protection = getProtection(repository, branch);

        assertThat(protection.getRequireReviews(), notNullValue());
        assertThat(protection.getRequireReviews().isEnabled(), equalTo(false));
    }

    public static void verifyRequireReviewsEnabled(Repository repository, String branch)
    {
        Protection protection = getProtection(repository, branch);

        assertThat(protection.getRequireReviews(), notNullValue());
        assertThat(protection.getRequireReviews().isEnabled(), equalTo(true));
        assertThat(protection.getRequireReviews().getRequiredReviewers(), equalTo(2));
        assertThat(protection.getRequireReviews().getDismissStaleApprovals(), equalTo(true));
        assertThat(protection.getRequireReviews().getRequireCodeOwner(), equalTo(true));
        assertThat(protection.getRequireReviews().enableRestrictDismissals(), equalTo(true));
    }

    public static void verifyRequireReviewsIsNull(Repository repository, String branch)
    {
        assertThat(getProtection(repository, branch).getRequireReviews(), notNullValue());
    }

    public static void verifyRequireSignedCommitsDisabled(Repository actual, String branch)
    {
        assertThat(getProtection(actual, branch).getRequireSignedCommits(), equalTo(false));
    }

    public static void verifyRequireSignedCommitsEnabled(Repository actual, String branch)
    {
        assertThat(getProtection(actual, branch).getRequireSignedCommits(), equalTo(true));
    }

    public static void verifyRequireSignedCommitsIsNull(Repository actual, String branch)
    {
        assertThat(getProtection(actual, branch).getRequireSignedCommits(), nullValue());
    }

    public static void verifyRequireUpToDateEnabled(Repository repository, String branch)
    {
        assertThat(getProtection(repository, branch).getRequiredChecks().getRequireUpToDate(), equalTo(true));
    }

    public static void verifySettingsIsNotNull(Repository actual)
    {
        assertThat(actual.getSettings(), notNullValue());
    }

    public static void verifyTeams(Repository actual, Map<String, Permission> expected)
    {
        verifyPermissions(actual.getSettings().getTeams(), expected);
    }

    public static void verifyTeams(Repository actual, Repository expected)
    {
        verifyTeams(actual, expected.getSettings().getTeams());
    }

    public static void verifyTeamsAreEmpty(Repository actual)
    {
        verifyPermissionsAreEmpty(actual.getSettings().getTeams());
    }

    private static Protection getProtection(Repository repository, String branch)
    {
        return repository.getSettings().getBranches().getProtection(branch);
    }

    private static void verifyPermissions(Map<String, Permission> actual, Map<String, Permission> expected)
    {
        assertThat(actual, notNullValue());
        assertThat(actual.size(), equalTo(1));
        assertThat(actual.keySet(), equalTo(expected.keySet()));
        assertThat(actual.values(), contains(expected.values().toArray()));
    }

    private static void verifyPermissionsAreEmpty(Map<String, Permission> actual)
    {
        assertThat(actual, notNullValue());
        assertThat(actual.isEmpty(), equalTo(true));
    }
}
