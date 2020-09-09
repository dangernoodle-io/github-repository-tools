package io.dangernoodle.grt.utils;

import static io.dangernoodle.RepositoryAsserts.verifyBranchProtectionDisabled;
import static io.dangernoodle.RepositoryAsserts.verifyBranchProtectionEnabled;
import static io.dangernoodle.RepositoryAsserts.verifyBranches;
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
import static io.dangernoodle.RepositoryAsserts.verifyPushAccessUnrestricted;
import static io.dangernoodle.RepositoryAsserts.verifyPushAccessUsers;
import static io.dangernoodle.RepositoryAsserts.verifyRebaseMerge;
import static io.dangernoodle.RepositoryAsserts.verifyRepositoryName;
import static io.dangernoodle.RepositoryAsserts.verifyRequireReviewsDisabled;
import static io.dangernoodle.RepositoryAsserts.verifyRequireReviewsDismissStaleApprovalsEnabled;
import static io.dangernoodle.RepositoryAsserts.verifyRequireReviewsDismissalRestrictionsEnabled;
import static io.dangernoodle.RepositoryAsserts.verifyRequireReviewsDismissalTeams;
import static io.dangernoodle.RepositoryAsserts.verifyRequireReviewsDismissalUsers;
import static io.dangernoodle.RepositoryAsserts.verifyRequireReviewsEnabled;
import static io.dangernoodle.RepositoryAsserts.verifyRequireReviewsRequireCodeOwnerEnabled;
import static io.dangernoodle.RepositoryAsserts.verifyRequireReviewsRequiredReviewers;
import static io.dangernoodle.RepositoryAsserts.verifyRequireSignedCommitsEnabled;
import static io.dangernoodle.RepositoryAsserts.verifyRequiredChecksContextsEnabled;
import static io.dangernoodle.RepositoryAsserts.verifyRequiredChecksRequireUpToDateEnabled;
import static io.dangernoodle.RepositoryAsserts.verifySquashMerge;
import static io.dangernoodle.RepositoryAsserts.verifyTeams;
import static io.dangernoodle.RepositoryAsserts.verifyWiki;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.Test;

import io.dangernoodle.RepositoryFiles;
import io.dangernoodle.grt.Repository;
import io.dangernoodle.grt.Repository.Settings.Color;
import io.dangernoodle.grt.Repository.Settings.Permission;
import io.dangernoodle.grt.utils.JsonTransformer.JsonObject;


public class RepositoryDeserlizationTest
{
    private Repository repository;

    private RepositoryFiles testFile;

    @Test
    public void testBranchesIsNull() throws Exception
    {
        givenNullBranches();
        whenParseIntoObject();
        thenBranchesIsNotNull();
    }

    @Test
    public void testBranchProtectionDisabled() throws Exception
    {
        // master: null
        givenADisabledBranchProtection();
        whenParseIntoObject();
        thenBranchProtectionIsDisabled();
    }

    @Test
    public void testBranchProtectionOnly() throws Exception
    {
        // master: { }
        givenOnlyABranchProtection();
        whenParseIntoObject();
        thenBranchProtectionIsEnabled();
    }

    @Test
    public void testRepository() throws Exception
    {
        givenARepository();
        whenParseIntoObject();
        thenRepositoryIsCorrect();
        thenPluginIsCorrect();
        thenWorkflowIsCorrect();
        thenPluginsIsCorrect();
    }

    @Test
    public void testRequireStatusChecks() throws Exception
    {
        givenRequiredStatusChecks();
        whenParseIntoObject();
        thenOnlyRequiredChecksAreEnabled();
    }

    private void givenADisabledBranchProtection()
    {
        testFile = RepositoryFiles.nullBranchProtection;
    }

    private void givenARepository()
    {
        testFile = RepositoryFiles.mockRepository;
    }

    private void givenNullBranches()
    {
        testFile = RepositoryFiles.noBranches;
    }

    private void givenOnlyABranchProtection()
    {
        testFile = RepositoryFiles.branchProtectionOnly;
    }

    private void givenRequiredStatusChecks()
    {
        testFile = RepositoryFiles.requireStatusChecks;
    }

    private void thenBranchesIsNotNull()
    {
        verifyBranches(repository);
    }

    private void thenBranchProtectionIsDisabled()
    {
        verifyBranchProtectionDisabled(repository, "master");
    }

    private void thenBranchProtectionIsEnabled()
    {
        verifyBranchProtectionEnabled(repository, "master");
    }

    private void thenOnlyRequiredChecksAreEnabled()
    {
        verifyRequiredChecksRequireUpToDateEnabled(repository, "master");
        verifyRequiredChecksContextsEnabled(repository, "master", Arrays.asList(repository.getName()));
        verifyRequireReviewsDisabled(repository, "master");
        verifyPushAccessUnrestricted(repository, "master");
    }

    private void thenPluginIsCorrect()
    {
        assertThat(repository.getPlugin("jenkins"), notNullValue());
        assertThat(repository.getPlugin("doesnotexist"), equalTo(JsonObject.NULL));
    }

    private void thenPluginsIsCorrect()
    {
        Map<String, JsonObject> plugins = repository.getPlugins();

        assertThat(plugins, notNullValue());
        assertThat(plugins.isEmpty(), equalTo(false));

        assertThat(plugins.containsKey("other"), equalTo(false));

        assertThat(plugins.containsKey("travis"), equalTo(true));
        assertThat(plugins.get("travis").getBoolean("enabled"), equalTo(true));
        assertThat(plugins.get("travis").getString("foo"), equalTo("bar"));

        assertThat(plugins.containsKey("jenkins"), equalTo(true));
        assertThat(plugins.get("jenkins").getString("container"), equalTo("maven"));
    }

    private void thenRepositoryIsCorrect()
    {
        verifyRepositoryName(repository, "grt-test-repository");
        verifyOrganization(repository, "dangernoodle-io");
        verifyDeleteBranchOnMerge(repository, true);
        verifyDescription(repository, "test repository");
        verifyFullName(repository, "dangernoodle-io/grt-test-repository");
        verifyHomepage(repository, "https://github.com/dangernoodle-io/grt-test-repository");
        verifyInitialized(repository, true);
        verifyIgnoreTemplate(repository, "Java");
        verifyIssues(repository, true);
        verifyLicenseTemplate(repository, "mit");
        verifyMergeCommits(repository, true);
        verifyRebaseMerge(repository, true);
        verifySquashMerge(repository, true);
        verifyPrivate(repository, true);
        verifyWiki(repository, true);
        verifyLabels(repository, Collections.singletonMap("label", Color.from("#006b75")));
        verifyTeams(repository, Collections.singletonMap("admin", Permission.admin));
        verifyCollaborators(repository, Collections.singletonMap("user", Permission.read));
        verifyPrimaryBranch(repository, "master");
        verifyOtherBranches(repository, "masteer", Arrays.asList("other"));
        verifyRequireSignedCommitsEnabled(repository, "master");
        verifyEnforeForAdministratorsEnabled(repository, "master");
        verifyRequireReviewsEnabled(repository, "master");
        verifyRequireReviewsDismissStaleApprovalsEnabled(repository, "master");
        verifyRequireReviewsRequiredReviewers(repository, "master", 2);
        verifyRequireReviewsRequireCodeOwnerEnabled(repository, "master");
        verifyRequireReviewsDismissalRestrictionsEnabled(repository, "master");
        verifyRequireReviewsDismissalTeams(repository, "master", Arrays.asList("team"));
        verifyRequireReviewsDismissalUsers(repository, "master", Arrays.asList("user"));
        verifyRequiredChecksRequireUpToDateEnabled(repository, "master");
        verifyRequiredChecksContextsEnabled(repository, "master", Arrays.asList("grt-test-repository"));
        verifyPushAccessRestricted(repository, "master");
        verifyPushAccessTeams(repository, "master", Arrays.asList("team"));
        verifyPushAccessUsers(repository, "master", Arrays.asList("user"));
    }

    private void thenWorkflowIsCorrect()
    {
        assertThat(repository.getWorkflow(), notNullValue());
    }

    private void whenParseIntoObject() throws IOException
    {
        repository = new Repository(testFile.toJsonObject());
    }
}
