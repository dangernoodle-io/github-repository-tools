package io.dangernoodle.grt.utils;

import static io.dangernoodle.RepositoryAsserts.verifyBranchProtectionDisabled;
import static io.dangernoodle.RepositoryAsserts.verifyBranchProtectionEnabled;
import static io.dangernoodle.RepositoryAsserts.verifyBranchesIsNotNull;
import static io.dangernoodle.RepositoryAsserts.verifyCollaborators;
import static io.dangernoodle.RepositoryAsserts.verifyEnforeForAdministratorsEnabled;
import static io.dangernoodle.RepositoryAsserts.verifyLabels;
import static io.dangernoodle.RepositoryAsserts.verifyOtherBranches;
import static io.dangernoodle.RepositoryAsserts.verifyPrimaryBranch;
import static io.dangernoodle.RepositoryAsserts.verifyPushAccessRestricted;
import static io.dangernoodle.RepositoryAsserts.verifyPushAccessTeams;
import static io.dangernoodle.RepositoryAsserts.verifyPushAccessUnrestricted;
import static io.dangernoodle.RepositoryAsserts.verifyPushAccessUsers;
import static io.dangernoodle.RepositoryAsserts.verifyRepositoryInitialized;
import static io.dangernoodle.RepositoryAsserts.verifyRepositoryIsPrivate;
import static io.dangernoodle.RepositoryAsserts.verifyRepositoryName;
import static io.dangernoodle.RepositoryAsserts.verifyRepositoryOrganization;
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
import static io.dangernoodle.RepositoryAsserts.verifyTeams;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.Test;

import io.dangernoodle.RepositoryFiles;
import io.dangernoodle.grt.Repository;
import io.dangernoodle.grt.Repository.Settings.Color;
import io.dangernoodle.grt.Repository.Settings.Permission;
import io.dangernoodle.grt.utils.JsonTransformer.JsonArray;
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
        verifyBranchesIsNotNull(repository);
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
        assertThat(repository.getPlugin("doesnotexist"), nullValue());
    }

    private void thenPluginsIsCorrect()
    {
        Map<String, Object> plugins = repository.getPlugins();

        assertThat(plugins, notNullValue());
        assertThat(plugins.isEmpty(), equalTo(false));

        assertThat(plugins.containsKey("other"), equalTo(false));

        assertThat(plugins.containsKey("travis"), equalTo(true));
        assertThat(((JsonObject) plugins.get("travis")).getBoolean("enabled"), equalTo(true));
        assertThat(((JsonObject) plugins.get("travis")).getString("foo"), equalTo("bar"));

        assertThat(plugins.containsKey("jenkins"), equalTo(true));
        assertThat(((JsonArray) plugins.get("jenkins")).iterator().hasNext(), equalTo(true));
    }

    private void thenRepositoryIsCorrect()
    {
        verifyRepositoryName(repository, "grt-test-repository");
        verifyRepositoryOrganization(repository, "dangernoodle-io");
        verifyRepositoryInitialized(repository);
        verifyRepositoryIsPrivate(repository);
        verifyLabels(repository, Collections.singletonMap("label", Color.from("#00000")));
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
