package io.dangernoodle.grt.utils;

import static io.dangernoodle.RepositoryAsserts.verifyBranchProtectionDisabled;
import static io.dangernoodle.RepositoryAsserts.verifyCollaborators;
import static io.dangernoodle.RepositoryAsserts.verifyDescription;
import static io.dangernoodle.RepositoryAsserts.verifyEnforeForAdministratorsEnabled;
import static io.dangernoodle.RepositoryAsserts.verifyHomepage;
import static io.dangernoodle.RepositoryAsserts.verifyLabels;
import static io.dangernoodle.RepositoryAsserts.verifyOrganization;
import static io.dangernoodle.RepositoryAsserts.verifyOtherBranches;
import static io.dangernoodle.RepositoryAsserts.verifyPrimaryBranch;
import static io.dangernoodle.RepositoryAsserts.verifyPushAccessRestricted;
import static io.dangernoodle.RepositoryAsserts.verifyPushAccessTeams;
import static io.dangernoodle.RepositoryAsserts.verifyPushAccessUsers;
import static io.dangernoodle.RepositoryAsserts.verifyRepositoryInitialized;
import static io.dangernoodle.RepositoryAsserts.verifyRepositoryIsPrivate;
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
import static io.dangernoodle.RepositoryAsserts.verifyTeams;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.dangernoodle.RepositoryFiles;
import io.dangernoodle.grt.Repository;
import io.dangernoodle.grt.Repository.Settings.Color;
import io.dangernoodle.grt.Repository.Settings.Permission;
import io.dangernoodle.grt.utils.JsonTransformer.JsonObject;


public class RepositoryBuilderTest
{
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

    private void givenARepository() throws IOException
    {
        expected = new Repository(RepositoryFiles.mockRepository.toJsonObject());

        builder.setName("grt-test-repository")
               .setOrganization("dangernoodle-io")
               .setDescription("test repository")
               .setHomepage("https://github.com/dangernoodle-io/grt-test-repository")
               .setInitialize(true)
               .setPrivate(true)
               .addLabel("label", Color.from("#00000"))
               .addTeam("admin", Permission.admin)
               .addCollaborator("user", Permission.read)
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
               .addPlugin("jenkins", toJson("container", "maven"))
               .addWorkflow("jenkins");
    }

    private void thenBranchProtectionIsNull()
    {
        verifyBranchProtectionDisabled(actual, "master");
    }

    private void thenRepositoriesMatch()
    {
        verifyRepositoryName(actual, expected);
        verifyOrganization(actual, expected);
        verifyDescription(actual, expected);
        verifyHomepage(actual, expected);
        verifyRepositoryInitialized(actual);
        verifyRepositoryIsPrivate(actual);
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

        assertThat(((JsonObject) actual.getPlugin("jenkins")).getString("container"), equalTo("maven"));
        assertThat(actual.getWorkflow().containsAll(expected.getWorkflow()), equalTo(true));
    }

    private JsonObject toJson(String key, String value)
    {
        return new JsonTransformer().serialize(Collections.singletonMap(key, value));
    }

    private void whenBuildRepository()
    {
        actual = builder.build();
    }
}
