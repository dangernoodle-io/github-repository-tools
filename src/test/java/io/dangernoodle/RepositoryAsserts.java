package io.dangernoodle;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.util.Collection;
import java.util.Map;

import io.dangernoodle.grt.Repository;
import io.dangernoodle.grt.Repository.Settings.AccessRestrictions;
import io.dangernoodle.grt.Repository.Settings.Branches.Protection;
import io.dangernoodle.grt.Repository.Settings.Branches.Protection.RequireReviews;
import io.dangernoodle.grt.Repository.Settings.Branches.Protection.RequiredChecks;
import io.dangernoodle.grt.Repository.Settings.Color;
import io.dangernoodle.grt.Repository.Settings.Permission;


public final class RepositoryAsserts
{
    public static void verifyBranches(Repository actual)
    {
        assertThat(actual.getSettings(), notNullValue());
        assertThat(actual.getSettings().getBranches(), notNullValue());
    }

    public static void verifyBranchProtectionDisabled(Repository actual, String branch)
    {
        assertThat(getProtection(actual, branch).isEnabled(), equalTo(false));
    }

    public static void verifyBranchProtectionEnabled(Repository actual, String branch)
    {
        assertThat(getProtection(actual, branch).isEnabled(), equalTo(true));
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

    public static void verifyDescription(Repository actual, Repository expected)
    {
        assertThat(actual.getDescription(), equalTo(expected.getDescription()));
    }

    public static void verifyDescription(Repository actual)
    {
        assertThat(actual.getDescription(), nullValue());
    }

    public static void verifyEnforeForAdministratorsDisabled(Repository actual, String branch)
    {
        assertThat(getProtection(actual, branch).getIncludeAdministrators(), equalTo(false));
    }

    public static void verifyEnforeForAdministratorsEnabled(Repository actual, String branch)
    {
        assertThat(getProtection(actual, branch).getIncludeAdministrators(), equalTo(true));
    }

    public static void verifyEnforeForAdministratorsNullValue(Repository actual, String branch)
    {
        assertThat(getProtection(actual, branch).getIncludeAdministrators(), nullValue());
    }

    public static void verifyHomepage(Repository actual, Repository expected)
    {
        assertThat(actual.getHomepage(), equalTo(expected.getHomepage()));
    }

    public static void verifyHomepage(Repository actual)
    {
        assertThat(actual.getHomepage(), nullValue());
    }

    public static void verifyLabels(Repository actual, Map<String, Color> expected)
    {
        verifyLabels(actual.getSettings().getLabels(), expected);
    }

    public static void verifyLabels(Repository actual, Repository expected)
    {
        verifyLabels(actual, expected.getSettings().getLabels());
    }

    public static void verifyLabelsAreEmpty(Map<String, Color> actual)
    {
        assertThat(actual, notNullValue());
        assertThat(actual.isEmpty(), equalTo(true));
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

    public static void verifyPushAccessRestricted(Repository actual, String branch)
    {
        assertThat(getProtection(actual, branch).hasRestrictedPushAccess(), equalTo(true));
    }

    public static void verifyPushAccessTeams(Repository actual, String branch, Collection<String> teams)
    {
        verifyRestrictedTeams(getProtection(actual, branch).getPushAccess(), teams);
    }

    public static void verifyPushAccessTeams(Repository actual, String branch, Repository expected)
    {
        verifyPushAccessTeams(actual, branch, getProtection(expected, branch).getPushAccess().getTeams());
    }

    public static void verifyPushAccessUnrestricted(Repository actual, String branch)
    {
        assertThat(getProtection(actual, branch).hasRestrictedPushAccess(), equalTo(false));
    }

    public static void verifyPushAccessUsers(Repository actual, String branch, Collection<String> users)
    {
        verifyRestrictedUsers(getProtection(actual, branch).getPushAccess(), users);
    }

    public static void verifyPushAccessUsers(Repository actual, String branch, Repository expected)
    {
        verifyPushAccessUsers(actual, branch, getProtection(expected, branch).getPushAccess().getUsers());
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

    public static void verifyRequiredChecksContextsDisabled(Repository actual, String branch)
    {
        assertThat(getProtection(actual, branch).getRequiredChecks().hasContexts(), equalTo(false));
    }

    public static void verifyRequiredChecksContextsEnabled(Repository actual, String branch, Collection<String> contexts)
    {
        assertThat(getRequiredChecks(actual, branch).getContexts().size(), equalTo(contexts.size()));
        assertThat(getRequiredChecks(actual, branch).getContexts(), contains(contexts.toArray()));
    }

    public static void verifyRequiredChecksContextsEnabled(Repository actual, String branch, Repository expected)
    {
        verifyRequiredChecksContextsEnabled(actual, branch, getRequiredChecks(expected, branch).getContexts());
    }

    public static void verifyRequiredChecksRequireUpToDateEnabled(Repository actual, String branch)
    {
        assertThat(getRequiredChecks(actual, branch).getRequireUpToDate(), equalTo(true));
    }

    public static void verifyRequiredStatusChecksDisabled(Repository actual, String branch)
    {
        assertThat(getRequiredChecks(actual, branch).hasContexts(), equalTo(false));
    }

    public static void verifyRequiredStatusChecksEnabled(Repository actual, String branch, Repository expected)
    {
        verifyRequiredChecksContextsEnabled(actual, branch, getRequiredChecks(expected, branch).getContexts());
    }

    public static void verifyRequireReviewsDisabled(Repository repository, String branch)
    {
        assertThat(getRequireReviews(repository, branch).isEnabled(), equalTo(false));
    }

    public static void verifyRequireReviewsDismissalRestrictionsEnabled(Repository actual, String branch)
    {
        assertThat(getRequireReviews(actual, branch).hasDismissalRestrictions(), equalTo(true));
    }

    public static void verifyRequireReviewsDismissalTeams(Repository actual, String branch, Collection<String> teams)
    {
        verifyRestrictedTeams(getProtection(actual, branch).getPushAccess(), teams);
    }

    public static void verifyRequireReviewsDismissalUsers(Repository actual, String branch, Collection<String> teams)
    {
        verifyRestrictedUsers(getProtection(actual, branch).getPushAccess(), teams);
    }

    public static void verifyRequireReviewsDismissalUsers(Repository actual, String branch, Repository expected)
    {
        verifyRequireReviewsDismissalUsers(actual, branch, getProtection(expected, branch).getPushAccess().getUsers());
    }

    public static void verifyRequireReviewsDismissStaleApprovalsEnabled(Repository actual, String branch)
    {
        assertThat(getRequireReviews(actual, branch).getDismissStaleApprovals(), equalTo(true));
    }

    public static void verifyRequireReviewsEnabled(Repository actual, String branch)
    {
        assertThat(getProtection(actual, branch).isEnabled(), equalTo(true));
    }

    public static void verifyRequireReviewsRequireCodeOwnerEnabled(Repository actual, String branch)
    {
        assertThat(getRequireReviews(actual, branch).getRequireCodeOwner(), equalTo(true));
    }

    public static void verifyRequireReviewsRequiredReviewers(Repository actual, String branch, int count)
    {
        assertThat(getRequireReviews(actual, branch).getRequiredReviewers(), equalTo(count));
    }

    public static void verifyRequireReviwsDismissalTeams(Repository actual, String branch, Repository expected)
    {
        verifyRequireReviewsDismissalTeams(actual, branch, getProtection(expected, branch).getPushAccess().getTeams());
    }

    public static void verifyRequireSignedCommitsDisabled(Repository actual, String branch)
    {
        assertThat(getProtection(actual, branch).getRequireSignedCommits(), equalTo(false));
    }

    public static void verifyRequireSignedCommitsEnabled(Repository actual, String branch)
    {
        assertThat(getProtection(actual, branch).getRequireSignedCommits(), equalTo(true));
    }

    public static void verifyRequireSignedCommits(Repository actual, String branch)
    {
        assertThat(getProtection(actual, branch).getRequireSignedCommits(), nullValue());
    }

    public static void verifyRequireUpToDateDisabled(Repository actual, String branch)
    {
        assertThat(getProtection(actual, branch).getRequiredChecks().getRequireUpToDate(), equalTo(false));
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

    private static RequiredChecks getRequiredChecks(Repository repository, String branch)
    {
        return getProtection(repository, branch).getRequiredChecks();
    }

    private static RequireReviews getRequireReviews(Repository actual, String branch)
    {
        return getProtection(actual, branch).getRequireReviews();
    }

    private static void verifyLabels(Map<String, Color> actual, Map<String, Color> expected)
    {
        assertThat(actual.keySet(), equalTo(expected.keySet()));
        assertThat(actual.values(), contains(expected.values().toArray()));
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

    private static void verifyRestrictedTeams(AccessRestrictions restrictions, Collection<String> teams)
    {
        assertThat(restrictions.isEnabled(), equalTo(true));
        assertThat(restrictions.hasTeams(), equalTo(!teams.isEmpty()));
        assertThat(restrictions.getTeams(), contains(teams.toArray()));
    }

    private static void verifyRestrictedUsers(AccessRestrictions restrictions, Collection<String> users)
    {
        assertThat(restrictions.isEnabled(), equalTo(true));
        assertThat(restrictions.hasUsers(), equalTo(!users.isEmpty()));
        assertThat(restrictions.getUsers(), contains(users.toArray()));
    }
}
