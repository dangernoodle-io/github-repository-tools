package io.dangernoodle.grt.repository;

import static io.dangernoodle.RepositoryAsserts.verifyArchived;
import static io.dangernoodle.RepositoryAsserts.verifyBranchProtectionDisabled;
import static io.dangernoodle.RepositoryAsserts.verifyCollaborators;
import static io.dangernoodle.RepositoryAsserts.verifyDeleteBranchOnMerge;
import static io.dangernoodle.RepositoryAsserts.verifyDescription;
import static io.dangernoodle.RepositoryAsserts.verifyEnforeForAdministratorsEnabled;
import static io.dangernoodle.RepositoryAsserts.verifyFullName;
import static io.dangernoodle.RepositoryAsserts.verifyHomepage;
import static io.dangernoodle.RepositoryAsserts.verifyIgnoreTemplate;
import static io.dangernoodle.RepositoryAsserts.verifyInitialized;
import static io.dangernoodle.RepositoryAsserts.verifyIssues;
import static io.dangernoodle.RepositoryAsserts.verifyLabels;
import static io.dangernoodle.RepositoryAsserts.verifyLicenseTemplate;
import static io.dangernoodle.RepositoryAsserts.verifyMergeCommits;
import static io.dangernoodle.RepositoryAsserts.verifyOrganization;
import static io.dangernoodle.RepositoryAsserts.verifyOtherBranches;
import static io.dangernoodle.RepositoryAsserts.verifyPrimaryBranch;
import static io.dangernoodle.RepositoryAsserts.verifyPrivate;
import static io.dangernoodle.RepositoryAsserts.verifyPushAccessRestricted;
import static io.dangernoodle.RepositoryAsserts.verifyPushAccessTeams;
import static io.dangernoodle.RepositoryAsserts.verifyPushAccessUsers;
import static io.dangernoodle.RepositoryAsserts.verifyRebaseMerge;
import static io.dangernoodle.RepositoryAsserts.verifyRepositoryName;
import static io.dangernoodle.RepositoryAsserts.verifyRequireReviewsDismissStaleApprovalsEnabled;
import static io.dangernoodle.RepositoryAsserts.verifyRequireReviewsDismissalRestrictionsEnabled;
import static io.dangernoodle.RepositoryAsserts.verifyRequireReviewsDismissalUsers;
import static io.dangernoodle.RepositoryAsserts.verifyRequireReviewsEnabled;
import static io.dangernoodle.RepositoryAsserts.verifyRequireReviewsRequireCodeOwnerEnabled;
import static io.dangernoodle.RepositoryAsserts.verifyRequireReviewsRequiredReviewers;
import static io.dangernoodle.RepositoryAsserts.verifyRequireReviwsDismissalTeams;
import static io.dangernoodle.RepositoryAsserts.verifyRequireSignedCommitsEnabled;
import static io.dangernoodle.RepositoryAsserts.verifyRequiredChecksContextsEnabled;
import static io.dangernoodle.RepositoryAsserts.verifyRequiredChecksRequireUpToDateEnabled;
import static io.dangernoodle.RepositoryAsserts.verifySquashMerge;
import static io.dangernoodle.RepositoryAsserts.verifyTeams;
import static io.dangernoodle.RepositoryAsserts.verifyWiki;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import com.google.common.collect.ImmutableMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.dangernoodle.RepositoryFiles;
import io.dangernoodle.grt.Repository;
import io.dangernoodle.grt.Repository.Settings.Color;
import io.dangernoodle.grt.Repository.Settings.Permission;
import io.dangernoodle.grt.util.JsonTransformer;


public class RepositoryBuilderTest
{
    private static List<String> WORKFLOWS = List.of("workflow1", "workflow2");

    private Repository actual;

    private RepositoryBuilder builder;

    private Repository expected;

    @BeforeEach
    public void before()
    {
        builder = new RepositoryBuilder(new JsonTransformer());
    }

    @Test
    public void testBuildCompleteRepository() throws Exception
    {
        givenARepository();
        whenBuildRepository();
        thenRepositoriesMatch();
    }

    @Test
    public void testDisableBranchProtection()
    {
        givenADisabledBranchProtection();
        whenBuildRepository();
        thenBranchProtectionIsNull();
    }

    private void givenADisabledBranchProtection()
    {
        builder.disableBranchProtection("master");
    }

    private void givenARepository() throws IOException, URISyntaxException
    {
        expected = new Repository(RepositoryFiles.mockRepository.toJsonObject());

        builder.setName("grt-test-repository")
               .setOrganization("dangernoodle-io")
               .setArchived(true)
               .setDeleteBranchOnMerge(true)
               .setDescription("test repository")
               .setHomepage("https://github.com/dangernoodle-io/grt-test-repository")
               .setIgnoreTemplate("Java")
               .setIssues(true)
               .setInitialize(true)
               .setLicenseTemplate("mit")
               .setMergeCommits(true)
               .setRebaseMerge(true)
               .setSquashMerge(true)
               .setPrivate(true)
               .setWiki(true)
               .addLabel("label", new Color("#006b75"))
               .addTeam("admin", new Permission("admin"))
               .addCollaborator("user", new Permission("read"))
               .setPrimaryBranch("master")
               .addOtherBranch("other")
               .requireSignedCommits("master", true)
               .enforceForAdminstrators("master", true)
               .requireReviews("master")
               .requiredReviewers("master", 2)
               .dismissStaleApprovals("master", true)
               .requireCodeOwnerReview("master", true)
               .addTeamReviewDismisser("master", "team")
               .addUserReviewDismisser("master", "user")
               .requireBranchUpToDate("master", true)
               .addRequiredContext("master", "grt-test-repository")
               .restrictPushAccess("master")
               .addTeamPushAccess("master", "team")
               .addUserPushAccess("master", "user")
               .addPlugin("jenkins", ImmutableMap.of("container", "maven"))
               .addWorkflow("command", WORKFLOWS);
    }

    private void thenBranchProtectionIsNull()
    {
        verifyBranchProtectionDisabled(actual, "master");
    }

    private void thenRepositoriesMatch()
    {
        verifyRepositoryName(actual, expected);
        verifyOrganization(actual, expected);
        verifyDeleteBranchOnMerge(actual, expected);
        verifyDescription(actual, expected);
        verifyFullName(actual, expected);
        verifyHomepage(actual, expected);
        verifyInitialized(actual, expected);
        verifyArchived(actual, expected);
        verifyIgnoreTemplate(actual, expected);
        verifyIssues(actual, expected);
        verifyLicenseTemplate(actual, expected);
        verifyMergeCommits(actual, expected);
        verifyRebaseMerge(actual, expected);
        verifySquashMerge(actual, expected);
        verifyPrivate(actual, expected);
        verifyWiki(actual, expected);
        verifyLabels(actual, expected);
        verifyTeams(actual, expected);
        verifyCollaborators(actual, expected);
        verifyPrimaryBranch(actual, "master");
        verifyOtherBranches(actual, "master", Arrays.asList("other"));
        verifyRequireSignedCommitsEnabled(actual, "master");
        verifyEnforeForAdministratorsEnabled(actual, "master");
        verifyRequireReviewsEnabled(actual, "master");
        verifyRequireReviewsDismissStaleApprovalsEnabled(actual, "master");
        verifyRequireReviewsRequiredReviewers(actual, "master", 2);
        verifyRequireReviewsRequireCodeOwnerEnabled(actual, "master");
        verifyRequireReviewsDismissalRestrictionsEnabled(actual, "master");
        verifyRequireReviwsDismissalTeams(actual, "master", expected);
        verifyRequireReviewsDismissalUsers(actual, "master", expected);
        verifyRequiredChecksRequireUpToDateEnabled(actual, "master");
        verifyRequiredChecksContextsEnabled(actual, "master", expected);
        verifyPushAccessRestricted(actual, "master");
        verifyPushAccessTeams(actual, "master", expected);
        verifyPushAccessUsers(actual, "master", expected);

        assertThat(actual.getPlugin("jenkins").getString("container"), equalTo("maven"));
        assertThat(actual.getWorkflows("command").containsAll(WORKFLOWS), equalTo(true));
    }

    private void whenBuildRepository()
    {
        actual = builder.build();
    }
}
