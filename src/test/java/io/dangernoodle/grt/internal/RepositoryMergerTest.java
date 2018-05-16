package io.dangernoodle.grt.internal;

import static io.dangernoodle.RepositoryAsserts.verifyBranchProtectionDisabled;
import static io.dangernoodle.RepositoryAsserts.verifyBranchProtectionEnabled;
import static io.dangernoodle.RepositoryAsserts.verifyCollaborators;
import static io.dangernoodle.RepositoryAsserts.verifyCollaboratorsAreEmpty;
import static io.dangernoodle.RepositoryAsserts.verifyEnforeForAdministratorsDisabled;
import static io.dangernoodle.RepositoryAsserts.verifyEnforeForAdministratorsEnabled;
import static io.dangernoodle.RepositoryAsserts.verifyLabels;
import static io.dangernoodle.RepositoryAsserts.verifyLabelsAreEmpty;
import static io.dangernoodle.RepositoryAsserts.verifyOrganization;
import static io.dangernoodle.RepositoryAsserts.verifyPrimaryBranch;
import static io.dangernoodle.RepositoryAsserts.verifyPushAccessRestricted;
import static io.dangernoodle.RepositoryAsserts.verifyPushAccessTeams;
import static io.dangernoodle.RepositoryAsserts.verifyPushAccessUnrestricted;
import static io.dangernoodle.RepositoryAsserts.verifyPushAccessUsers;
import static io.dangernoodle.RepositoryAsserts.verifyRepositoryInitialized;
import static io.dangernoodle.RepositoryAsserts.verifyRepositoryNotInitialized;
import static io.dangernoodle.RepositoryAsserts.verifyRequireReviewsDisabled;
import static io.dangernoodle.RepositoryAsserts.verifyRequireReviewsDismissStaleApprovalsEnabled;
import static io.dangernoodle.RepositoryAsserts.verifyRequireReviewsDismissalRestrictionsEnabled;
import static io.dangernoodle.RepositoryAsserts.verifyRequireReviewsDismissalUsers;
import static io.dangernoodle.RepositoryAsserts.verifyRequireReviewsEnabled;
import static io.dangernoodle.RepositoryAsserts.verifyRequireReviewsRequireCodeOwnerEnabled;
import static io.dangernoodle.RepositoryAsserts.verifyRequireReviewsRequiredReviewers;
import static io.dangernoodle.RepositoryAsserts.verifyRequireReviwsDismissalTeams;
import static io.dangernoodle.RepositoryAsserts.verifyRequireSignedCommitsDisabled;
import static io.dangernoodle.RepositoryAsserts.verifyRequireSignedCommitsEnabled;
import static io.dangernoodle.RepositoryAsserts.verifyRequiredChecksContextsEnabled;
import static io.dangernoodle.RepositoryAsserts.verifyRequiredChecksRequireUpToDateEnabled;
import static io.dangernoodle.RepositoryAsserts.verifyRequiredStatusChecksDisabled;
import static io.dangernoodle.RepositoryAsserts.verifyTeams;
import static io.dangernoodle.RepositoryAsserts.verifyTeamsAreEmpty;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.dangernoodle.grt.Repository;
import io.dangernoodle.grt.Repository.Settings.Color;
import io.dangernoodle.grt.Repository.Settings.Permission;
import io.dangernoodle.grt.json.JsonTransformer;
import io.dangernoodle.grt.json.JsonTransformer.JsonObject;


public class RepositoryMergerTest
{
    private RepositoryBuilder dBuilder;

    private Repository defaults;

    private Exception exception;

    private RepositoryBuilder oBuilder;

    private Repository overrides;

    private String primaryBranch;

    private Repository repository;

    @BeforeEach
    public void setup()
    {
        primaryBranch = "master";

        dBuilder = new RepositoryBuilder();
        oBuilder = new RepositoryBuilder();

        // these are required values, so just set defaults here
        oBuilder.setName("repository");
        dBuilder.setOrganization("default-org");
    }

    @Test
    public void testBranchProtectionBothDisable()
    {
        givenBranchProtectionDefaultsDisables();
        givenBranchProtectionOverrideDisables();
        whenBuildRepositories();
        thenBranchProtectionIsNotEnabled();
    }

    @Test
    public void testBranchProtectionDefaults()
    {
        givenBranchProtectionDefaults();
        whenBuildRepositories();
        thenBranchProtectionsAreEnabled(defaults);
    }

    @Test
    public void testBranchProtectionDefaultsDisables()
    {
        givenBranchProtectionDefaultsDisables();
        whenBuildRepositories();
        thenBranchProtectionIsNotEnabled();
    }

    @Test
    public void testBranchProtectionOnlyOverride()
    {
        givenBranchProtectionDefaults();
        givenBranchProtectionOnlyOverride();
        whenBuildRepositories();
        thenBranchProtectionOnlyIsEnabled(repository);
    }

    @Test
    public void testBranchProtectionOverrideDisables()
    {
        givenBranchProtectionDefaults();
        givenBranchProtectionOverrideDisables();
        whenBuildRepositories();
        thenBranchProtectionIsNotEnabled();
    }

    @Test
    public void testBranchProtectionOverrideEnforceForAdmins()
    {
        givenBranchProtectionDefaults();
        givenBranchProtectionOverridesEnforceForAdmins();
        whenBuildRepositories();
        thenEnforceForAdminsDisabled();
        thenOtherProtectionsAreDisabled();
    }

    @Test
    public void testBranchProtectionOverrides()
    {
        givenBranchProtectionOverrides();
        whenBuildRepositories();
        thenBranchProtectionsAreEnabled(overrides);
    }

    @Test
    public void testBranchProtectionOverrideSignedCommits()
    {
        givenBranchProtectionDefaults();
        givenBranchProtectionOverridesSignedCommits();
        whenBuildRepositories();
        thenSignedCommitsAreDisabled();
        thenEnforceForAdminsDisabled();
        thenOtherProtectionsAreDisabled();
    }

    @Test
    public void testBranchProtectionRequireUpToDateOnly()
    {
        givenBranchProtectionDefaultRequireUpToDateOnly();
        whenBuildRepositories();
        thenBranchProtectionDefaultRequireUpToDateOnly();
    }

    @Test
    public void testCollaboratorDefaults()
    {
        givenCollaboratorDefaults();
        whenBuildRepositories();
        thenCollaboratorsAreReturned(defaults);
    }

    @Test
    public void testCollaboratorOverrides()
    {
        givenCollaboratorDefaults();
        givenCollaboratorOverrides();
        whenBuildRepositories();
        thenCollaboratorsAreReturned(overrides);
    }

    @Test
    public void testCollaboratorOverridesEmpty()
    {
        givenCollaboratorDefaults();
        givenCollaboratorOverridesAreEmpty();
        whenBuildRepositories();
        thenCollaboratorsAreEmpty();
    }

    @Test
    public void testCollaboratorOverridesNull()
    {
        givenCollaboratorDefaults();
        givenCollaboratorOverridesAreNull();
        whenBuildRepositories();
        thenCollaboratorsAreEmpty();
    }

    @Test
    public void testInitializeDefault()
    {
        givenInitializeDefault();
        whenBuildRepositories();
        thenInitializationIsTrue();
    }

    @Test
    public void testInitializeOverride()
    {
        givenInitializeDefault();
        givenInitializeOverride();
        whenBuildRepositories();
        thenInitializationIsFalse();
    }

    @Test
    public void testLabelDefaults()
    {
        givenLabelDefaults();
        whenBuildRepositories();
        thenLabelsAreReturned(defaults);
    }

    @Test
    public void testLabelOverrides()
    {
        givenLabelDefaults();
        givenLabelOverrides();
        whenBuildRepositories();
        thenLabelsAreReturned(overrides);
    }

    @Test
    public void testLabelOverridesEmpty()
    {
        givenLabelDefaults();
        givenLabelOverridesEmpty();
        whenBuildRepositories();
        thenLabelsAreEmpty();
    }

    @Test
    public void testLabelOverridesNull()
    {
        givenLabelDefaults();
        givenLabelOverridesNull();
        whenBuildRepositories();
        thenLabelsAreEmpty();
    }

    @Test
    public void testOrganizationDefault()
    {
        // see #setup()
        whenBuildRepositories();
        thenOrganizationDefaultIsReturned();
    }

    @Test
    public void testOrganizationNull()
    {
        givenNullOrganizations();
        whenBuildRepositories();
        thenIllegalStateExceptionThrown();
    }

    @Test
    public void testOrganizationOverride()
    {
        givenAnOrganizationOverride();
        whenBuildRepositories();
        thenOrganizationOverrideIsReturned();
    }

    @Test
    public void testPluginDefaults()
    {
        givenADefaultPlugin();
        whenBuildRepositories();
        thenDefaultPluginsReturned();
    }

    @Test
    public void testPluginOverrides()
    {
        givenADefaultPlugin();
        givenAnOverridenPlugin();
        whenBuildRepositories();
        thenOverridePluginsReturned();
    }

    @Test
    public void testPrimaryBranchDefault()
    {
        givenAPrimaryBranchDefault();
        whenBuildRepositories();
        thenPrimaryBranchIsCorrect();
    }

    @Test
    public void testPrimaryBranchDefaultsToMaster()
    {
        givenPrimaryBranchNoSet();
        whenBuildRepositories();
        thenPrimaryBranchIsCorrect();
    }

    @Test
    public void testPrimaryBranchOverride()
    {
        givenAPrimaryBranchOverride();
        whenBuildRepositories();
        thenPrimaryBranchIsCorrect();
    }

    @Test
    public void testRepositoryDefaults()
    {
        whenBuildRepositories();
        thenInitializationIsFalse();
        thenLabelsAreEmpty();
        thenTeamsAreEmpty();
        thenCollaboratorsAreEmpty();
        thenPrimaryBranchIsCorrect();
        thenBranchProtectionIsNotEnabled();
    }

    @Test
    public void testTeamDefaults()
    {
        givenTeamDefaults();
        whenBuildRepositories();
        thenTeamsAreReturned(defaults);
    }

    @Test
    public void testTeamOverrides()
    {
        givenTeamDefaults();
        givenTeamOverrides();
        whenBuildRepositories();
        thenTeamsAreReturned(overrides);
    }

    @Test
    public void testTeamOverridesEmpty()
    {
        givenTeamDefaults();
        givenTeamOverridesAreEmpty();
        whenBuildRepositories();
        thenTeamsAreEmpty();
    }

    @Test
    public void testTeamOverridesNull()
    {
        givenTeamDefaults();
        givenTeamOverridesAreNull();
        whenBuildRepositories();
        thenTeamsAreEmpty();
    }

    @Test
    public void testWorkflowDefault()
    {
        givenDefaultWorkflow();
        whenBuildRepositories();
        thenDefaultWorkflowReturned();
    }

    @Test
    public void testWorkflowOverride()
    {
        givenDefaultWorkflow();
        givenOverrideWorkflow();
        whenBuildRepositories();
        thenOverrideWorkflowReturned();
    }

    private void givenADefaultPlugin()
    {
        dBuilder.addPlugin("default", toJson("{\"foo\": \"bar\"}"))
                .addPlugin("other", toJson("{\"foo\": \"bar\"}"));
    }

    private void givenAnOrganizationOverride()
    {
        oBuilder.setOrganization("override-org");
    }

    private void givenAnOverridenPlugin()
    {
        dBuilder.addPlugin("default", toJson("{\"foo\": \"baz\"}"))
                .addPlugin("override", toJson("{\"foo\": \"baz\"}"));
    }

    private void givenAPrimaryBranchDefault()
    {
        primaryBranch = "primary";
        dBuilder.setPrimaryBranch(primaryBranch);
    }

    private void givenAPrimaryBranchOverride()
    {
        primaryBranch = "override";
        oBuilder.setPrimaryBranch(primaryBranch);
    }

    private void givenBranchProtectionDefaultRequireUpToDateOnly()
    {
        dBuilder.requireBranchUpToDate("master", true);
    }

    private void givenBranchProtectionDefaults()
    {
        setEnabledBranchProtections(dBuilder);
    }

    private void givenBranchProtectionDefaultsDisables()
    {
        dBuilder.disableBranchProtection("master");
    }

    private void givenBranchProtectionOnlyOverride()
    {
        oBuilder.enableBranchProtection("master");
    }

    private void givenBranchProtectionOverrideDisables()
    {
        oBuilder.disableBranchProtection("master");
    }

    private void givenBranchProtectionOverrides()
    {
        setEnabledBranchProtections(oBuilder);
    }

    private void givenBranchProtectionOverridesEnforceForAdmins()
    {
        oBuilder.enableBranchProtection("master")
                .enforceForAdminstrators("master", false);
    }

    private void givenBranchProtectionOverridesSignedCommits()
    {
        oBuilder.enableBranchProtection("master")
                .requireSignedCommits("master", false);
    }

    private void givenCollaboratorDefaults()
    {
        dBuilder.addCollaborator("default", Permission.write);
    }

    private void givenCollaboratorOverrides()
    {
        oBuilder.addCollaborator("override", Permission.admin);
    }

    private void givenCollaboratorOverridesAreEmpty()
    {
        oBuilder.addCollaborators(Collections.emptyMap());
    }

    private void givenCollaboratorOverridesAreNull()
    {
        oBuilder.addCollaborators();
    }

    private void givenDefaultWorkflow()
    {
        dBuilder.addWorkflow("default");
    }

    private void givenInitializeDefault()
    {
        dBuilder.setInitialize(true);
    }

    private void givenInitializeOverride()
    {
        oBuilder.setInitialize(false);
    }

    private void givenLabelDefaults()
    {
        dBuilder.addLabel("default", Color.from("#00000"));
    }

    private void givenLabelOverrides()
    {
        oBuilder.addLabel("override", Color.from("#00001"));
    }

    private void givenLabelOverridesEmpty()
    {
        oBuilder.addLabels(Collections.emptyMap());
    }

    private void givenLabelOverridesNull()
    {
        oBuilder.addLabels();
    }

    private void givenNullOrganizations()
    {
        dBuilder.setOrganization(null);
        oBuilder.setOrganization(null);
    }

    private void givenOverrideWorkflow()
    {
        oBuilder.addWorkflow("override");
    }

    private void givenPrimaryBranchNoSet()
    {
        primaryBranch = "master";
        // nothing set on builders...
    }

    private void givenTeamDefaults()
    {
        dBuilder.addTeam("default", Permission.write);
    }

    private void givenTeamOverrides()
    {
        oBuilder.addTeam("override", Permission.admin);
    }

    private void givenTeamOverridesAreEmpty()
    {
        oBuilder.addTeams(Collections.emptyMap());
    }

    private void givenTeamOverridesAreNull()
    {
        oBuilder.addTeams();
    }

    private void setEnabledBranchProtections(RepositoryBuilder builder)
    {
        builder.enableBranchProtection("master")
               .requireSignedCommits("master", true)
               .enforceForAdminstrators("master", true);

        setEnableRequiredReviews(builder);
        setEnableRequiredChecks(builder);
        setEnableRestricedPushAccess(builder);
    }

    private void setEnableRequiredChecks(RepositoryBuilder builder)
    {
        builder.enableBranchProtection("master")
               .requireBranchUpToDate("master", true)
               .addRequiredContext("master", "repository");
    }

    private void setEnableRequiredReviews(RepositoryBuilder builder)
    {
        builder.enableBranchProtection("master")
               .requireReviews("master")
               .requiredReviewers("master", 2)
               .dismissStaleApprovals("master", true)
               .requireCodeOwnerReview("master", true)
               .addTeamReviewDismisser("master", "default")
               .addUserReviewDismisser("master", "default");
    }

    private void setEnableRestricedPushAccess(RepositoryBuilder builder)
    {
        builder.enableBranchProtection("master")
               .restrictPushAccess("master")
               .addTeamPushAccess("master", "default")
               .addUserPushAccess("master", "default");
    }

    private void thenBranchProtectionDefaultRequireUpToDateOnly()
    {
        verifyRequiredChecksRequireUpToDateEnabled(repository, "master");
        verifyRequiredStatusChecksDisabled(repository, "master");

        verifyRequireReviewsDisabled(repository, "master");
        verifyPushAccessUnrestricted(repository, "master");
    }

    private void thenBranchProtectionIsNotEnabled()
    {
        verifyBranchProtectionDisabled(repository, "master");
    }

    private void thenBranchProtectionOnlyIsEnabled(Repository repository)
    {
        verifyBranchProtectionEnabled(repository, "master");
    }

    private void thenBranchProtectionsAreEnabled(Repository expected)
    {
        verifyRequireSignedCommitsEnabled(repository, "master");
        verifyEnforeForAdministratorsEnabled(repository, "master");
        verifyRequireReviewsEnabled(repository, "master");
        verifyRequireReviewsDismissStaleApprovalsEnabled(repository, "master");
        verifyRequireReviewsRequiredReviewers(repository, "master", 2);
        verifyRequireReviewsRequireCodeOwnerEnabled(repository, "master");
        verifyRequireReviewsDismissalRestrictionsEnabled(repository, "master");
        verifyRequireReviwsDismissalTeams(repository, "master", expected);
        verifyRequireReviewsDismissalUsers(repository, "master", expected);
        verifyRequiredChecksRequireUpToDateEnabled(repository, "master");
        verifyRequiredChecksContextsEnabled(repository, "master", expected);
        verifyPushAccessRestricted(repository, "master");
        verifyPushAccessTeams(repository, "master", expected);
        verifyPushAccessUsers(repository, "master", expected);
    }

    private void thenCollaboratorsAreEmpty()
    {
        verifyCollaboratorsAreEmpty(repository);
    }

    private void thenCollaboratorsAreReturned(Repository expected)
    {
        verifyCollaborators(repository, expected);
    }

    private void thenDefaultPluginsReturned()
    {
        assertThat(repository.getPlugins().size(), equalTo(2));
        assertThat(((JsonObject) repository.getPlugin("default")).getString("foo"), equalTo("bar"));
    }

    private void thenDefaultWorkflowReturned()
    {
        assertThat(repository.getWorkflow(), contains(defaults.getWorkflow().toArray()));
    }

    private void thenEnforceForAdminsDisabled()
    {
        verifyEnforeForAdministratorsDisabled(repository, "master");
    }

    private void thenIllegalStateExceptionThrown()
    {
        assertThat(exception, notNullValue());
        assertThat(exception, instanceOf(IllegalStateException.class));
    }

    private void thenInitializationIsFalse()
    {
        verifyRepositoryNotInitialized(repository);
    }

    private void thenInitializationIsTrue()
    {
        verifyRepositoryInitialized(repository);
    }

    private void thenLabelsAreEmpty()
    {
        verifyLabelsAreEmpty(repository.getSettings().getLabels());
    }

    private void thenLabelsAreReturned(Repository expected)
    {
        verifyLabels(repository, expected);
    }

    private void thenOrganizationDefaultIsReturned()
    {
        verifyOrganization(repository, defaults);
    }

    private void thenOrganizationOverrideIsReturned()
    {
        verifyOrganization(repository, overrides);
    }

    private void thenOtherProtectionsAreDisabled()
    {
        verifyRequireReviewsDisabled(repository, "master");
        verifyRequiredStatusChecksDisabled(repository, "master");
        verifyPushAccessUnrestricted(repository, "master");
    }

    private void thenOverridePluginsReturned()
    {
        assertThat(repository.getPlugins().size(), equalTo(3));
        assertThat(((JsonObject) repository.getPlugin("default")).getString("foo"), equalTo("baz"));
    }

    private void thenOverrideWorkflowReturned()
    {
        assertThat(repository.getWorkflow(), contains(overrides.getWorkflow().toArray()));
    }

    private void thenPrimaryBranchIsCorrect()
    {
        verifyPrimaryBranch(repository, primaryBranch);
    }

    private void thenSignedCommitsAreDisabled()
    {
        verifyRequireSignedCommitsDisabled(repository, "master");
    }

    private void thenTeamsAreEmpty()
    {
        verifyTeamsAreEmpty(repository);
    }

    private void thenTeamsAreReturned(Repository expected)
    {
        verifyTeams(repository, expected);
    }

    private JsonObject toJson(String json)
    {
        return JsonTransformer.deserialize(json);
    }

    private void whenBuildRepositories()
    {
        defaults = dBuilder.build();
        overrides = oBuilder.build();

        try
        {
            repository = new RepositoryMerger(defaults, overrides).merge();
        }
        catch (Exception e)
        {
            exception = e;
        }
    }
}
