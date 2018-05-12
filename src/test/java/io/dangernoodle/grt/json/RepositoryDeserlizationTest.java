package io.dangernoodle.grt.json;

import static io.dangernoodle.TestAsserts.verifyBranchProtectionEnabledOnly;
import static io.dangernoodle.TestAsserts.verifyBranchProtectionIsDisabled;
import static io.dangernoodle.TestAsserts.verifyBranchProtectionsAreAllNull;
import static io.dangernoodle.TestAsserts.verifyBranchesIsNotNull;
import static io.dangernoodle.TestAsserts.verifyCollaborators;
import static io.dangernoodle.TestAsserts.verifyEnforeForAdministratorsEnabled;
import static io.dangernoodle.TestAsserts.verifyLabels;
import static io.dangernoodle.TestAsserts.verifyOtherBranches;
import static io.dangernoodle.TestAsserts.verifyPrimaryBranch;
import static io.dangernoodle.TestAsserts.verifyPushAccessIsRestricted;
import static io.dangernoodle.TestAsserts.verifyPushAccessIsUnrestricted;
import static io.dangernoodle.TestAsserts.verifyPushAccessTeams;
import static io.dangernoodle.TestAsserts.verifyPushAccessUsers;
import static io.dangernoodle.TestAsserts.verifyRepositoryInitialized;
import static io.dangernoodle.TestAsserts.verifyRepositoryIsPrivate;
import static io.dangernoodle.TestAsserts.verifyRepositoryName;
import static io.dangernoodle.TestAsserts.verifyRepositoryOrganization;
import static io.dangernoodle.TestAsserts.verifyRequireReviewsEnabled;
import static io.dangernoodle.TestAsserts.verifyRequireReviewsIsNull;
import static io.dangernoodle.TestAsserts.verifyRequireSignedCommitsEnabled;
import static io.dangernoodle.TestAsserts.verifyRequiredStatusChecksEnabled;
import static io.dangernoodle.TestAsserts.verifySettingsIsNotNull;
import static io.dangernoodle.TestAsserts.verifyTeams;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.Test;

import io.dangernoodle.TestFiles;
import io.dangernoodle.grt.Repository;
import io.dangernoodle.grt.Repository.Color;
import io.dangernoodle.grt.Repository.Permission;
import io.dangernoodle.grt.json.JsonTransformer.JsonArray;
import io.dangernoodle.grt.json.JsonTransformer.JsonObject;


public class RepositoryDeserlizationTest
{
    private Repository repository;

    private TestFiles testFile;

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
    public void testBranchProtectionsAllNull() throws Exception
    {
        // master: { requiredReviews: null, requiredStatusChecks: null, pushAccess: null }
        givenNullBranchProtections();
        whenParseIntoObject();
        thenBranchProtectionsAreNull();
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

    @Test
    public void testRequireStatusChecks() throws Exception
    {
        givenRequiredStatusChecks();
        whenParseIntoObject();
        thenOnlyRequiredChecksAreEnabled();
    }

    @Test
    public void testSettingsIsNull() throws Exception
    {
        givenNullSettings();
        whenParseIntoObject();
        thenSettingsIsNotNull();
    }

    private void givenADisabledBranchProtection()
    {
        testFile = TestFiles.nullBranchProtection;
    }

    private void givenARepository()
    {
        testFile = TestFiles.mockRepository;
    }

    private void givenNullBranches()
    {
        testFile = TestFiles.noBranches;
    }

    private void givenNullBranchProtections()
    {
        testFile = TestFiles.nullBranchProtections;
    }

    private void givenNullSettings()
    {
        // cheat - it has no 'settings' element
        testFile = TestFiles.nullWorkflow;
    }

    private void givenOnlyABranchProtection()
    {
        testFile = TestFiles.branchProtectionOnly;
    }

    private void givenRequiredStatusChecks()
    {
        testFile = TestFiles.requireStatusChecks;
    }

    private void thenBranchesIsNotNull()
    {
        verifyBranchesIsNotNull(repository);
    }

    private void thenBranchProtectionIsDisabled()
    {
        verifyBranchProtectionIsDisabled(repository, "master");
    }

    private void thenBranchProtectionIsEnabled()
    {
        verifyBranchProtectionEnabledOnly(repository, "master");
    }

    private void thenBranchProtectionsAreNull()
    {
        verifyBranchProtectionsAreAllNull(repository, "master");
    }

    private void thenOnlyRequiredChecksAreEnabled()
    {
        verifyRequiredStatusChecksEnabled(repository, "master");
        verifyRequireReviewsIsNull(repository, "master");
        verifyPushAccessIsUnrestricted(repository, "master");
    }

    private void thenPluginIsCorrect()
    {
       assertThat(repository.getPlugin("jenkins"), notNullValue());
       assertThat(repository.getPlugin("doesnotexist"), nullValue());
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
        verifyRequiredStatusChecksEnabled(repository, "master");
        verifyPushAccessIsRestricted(repository, "master");
        verifyPushAccessTeams(repository, "master", Arrays.asList("write"));
        verifyPushAccessUsers(repository, "master", Arrays.asList("user"));
    }

    private void thenSettingsIsNotNull()
    {
        verifySettingsIsNotNull(repository);
    }

    private void thenWorkflowIsCorrect()
    {
        assertThat(repository.getWorkflow(), notNullValue());
    }

    private void whenParseIntoObject() throws IOException
    {
        repository = Repository.load(testFile.getFile());
    }
}
