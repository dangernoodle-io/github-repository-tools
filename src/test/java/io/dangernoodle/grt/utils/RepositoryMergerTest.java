package io.dangernoodle.grt.utils;

import static io.dangernoodle.RepositoryAsserts.verifyArchived;
import static io.dangernoodle.RepositoryAsserts.verifyBranchProtectionDisabled;
import static io.dangernoodle.RepositoryAsserts.verifyBranchProtectionEnabled;
import static io.dangernoodle.RepositoryAsserts.verifyCollaborators;
import static io.dangernoodle.RepositoryAsserts.verifyCollaboratorsAreEmpty;
import static io.dangernoodle.RepositoryAsserts.verifyDeleteBranchOnMerge;
import static io.dangernoodle.RepositoryAsserts.verifyDescription;
import static io.dangernoodle.RepositoryAsserts.verifyEnforeForAdministratorsDisabled;
import static io.dangernoodle.RepositoryAsserts.verifyEnforeForAdministratorsEnabled;
import static io.dangernoodle.RepositoryAsserts.verifyHomepage;
import static io.dangernoodle.RepositoryAsserts.verifyInitialized;
import static io.dangernoodle.RepositoryAsserts.verifyIssues;
import static io.dangernoodle.RepositoryAsserts.verifyLabels;
import static io.dangernoodle.RepositoryAsserts.verifyLabelsAreEmpty;
import static io.dangernoodle.RepositoryAsserts.verifyMergeCommits;
import static io.dangernoodle.RepositoryAsserts.verifyOrganization;
import static io.dangernoodle.RepositoryAsserts.verifyPrimaryBranch;
import static io.dangernoodle.RepositoryAsserts.verifyPushAccessRestricted;
import static io.dangernoodle.RepositoryAsserts.verifyPushAccessTeams;
import static io.dangernoodle.RepositoryAsserts.verifyPushAccessUnrestricted;
import static io.dangernoodle.RepositoryAsserts.verifyPushAccessUsers;
import static io.dangernoodle.RepositoryAsserts.verifyRebaseMerge;
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
import static io.dangernoodle.RepositoryAsserts.verifySquashMerge;
import static io.dangernoodle.RepositoryAsserts.verifyTeams;
import static io.dangernoodle.RepositoryAsserts.verifyTeamsAreEmpty;
import static io.dangernoodle.RepositoryAsserts.verifyWiki;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;

import java.util.Collections;

import com.google.common.collect.ImmutableMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.dangernoodle.grt.Repository;
import io.dangernoodle.grt.Repository.Settings.Color;
import io.dangernoodle.grt.Repository.Settings.Permission;
import io.dangernoodle.grt.utils.JsonTransformer.JsonObject;


public class RepositoryMergerTest
{
    private static final JsonTransformer transformer = new JsonTransformer();

    private RepositoryBuilder deBuilder;

    private Repository defaults;

    private Exception exception;

    private RepositoryMerger merger;

    private RepositoryBuilder ovBuilder;

    private Repository overrides;

    private String primaryBranch;

    private Repository repository;

    @BeforeEach
    public void setup()
    {
        primaryBranch = "master";

        deBuilder = createBuilder();
        ovBuilder = createBuilder();

        // these are required values, so just set defaults here
        ovBuilder.setName("repository");
        deBuilder.setOrganization("default-org");

        merger = new RepositoryMerger(transformer);
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
    public void testDescriptionDefaults()
    {
        givenADefaultDecription();
        whenBuildRepositories();
        thenDescriptionIsNull();
    }

    @Test
    public void testDescriptionOverrides()
    {
        givenADefaultDecription();
        givenAnOverrideDescription();
        whenBuildRepositories();
        thenDescriptionIsCorrect();
    }

    @Test
    public void testHomepageDefaults()
    {
        givenADefaultHomepage();
        whenBuildRepositories();
        thenHompageIsNull();
    }

    @Test
    public void testHomepageOverrides()
    {
        givenADefaultHomepage();
        givenAnOverrideHomepage();
        whenBuildRepositories();
        thenHompageIsCorrect();
    }

    @Test
    public void testImplicitDefaults()
    {
        whenBuildRepositories();
        thenImplicitDefaultsAreCorrect();
    }

    @Test
    public void testImplicitOverrides()
    {
        givenImplictsAreOverridden();
        whenBuildRepositories();
        thenImplicitsAreOverridden();
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

    private RepositoryBuilder createBuilder()
    {
        return new RepositoryBuilder(transformer);
    }

    private void givenADefaultDecription()
    {
        deBuilder.setDescription("default");
    }

    private void givenADefaultHomepage()
    {
        deBuilder.setHomepage("default");
    }

    private void givenADefaultPlugin()
    {
        deBuilder.addPlugin("p1", ImmutableMap.of("enabled", true))
                 .addPlugin("p2", ImmutableMap.of("enabled", false))
                 .addPlugin("p3", ImmutableMap.of("enabled", true, "object", ImmutableMap.of("k1", 1, "k2", "v")));
    }

    private void givenAnOrganizationOverride()
    {
        ovBuilder.setOrganization("override-org");
    }

    private void givenAnOverrideDescription()
    {
        ovBuilder.setDescription("override");
    }

    private void givenAnOverrideHomepage()
    {
        ovBuilder.setHomepage("override");
    }

    private void givenAnOverridenPlugin()
    {
        ovBuilder.addPlugin("p2", ImmutableMap.of("enabled", true))
                 .addPlugin("p3", ImmutableMap.of("enabled", false, "object", ImmutableMap.of("k1", 5, "k3", "3")))
                 .addPlugin("p4", ImmutableMap.of("enabled", true));
    }

    private void givenAPrimaryBranchDefault()
    {
        primaryBranch = "primary";
        deBuilder.setPrimaryBranch(primaryBranch);
    }

    private void givenAPrimaryBranchOverride()
    {
        primaryBranch = "override";
        ovBuilder.setPrimaryBranch(primaryBranch);
    }

    private void givenBranchProtectionDefaultRequireUpToDateOnly()
    {
        deBuilder.requireBranchUpToDate("master", true);
    }

    private void givenBranchProtectionDefaults()
    {
        setEnabledBranchProtections(deBuilder);
    }

    private void givenBranchProtectionDefaultsDisables()
    {
        deBuilder.disableBranchProtection("master");
    }

    private void givenBranchProtectionOnlyOverride()
    {
        ovBuilder.enableBranchProtection("master");
    }

    private void givenBranchProtectionOverrideDisables()
    {
        ovBuilder.disableBranchProtection("master");
    }

    private void givenBranchProtectionOverrides()
    {
        setEnabledBranchProtections(ovBuilder);
    }

    private void givenBranchProtectionOverridesEnforceForAdmins()
    {
        ovBuilder.enableBranchProtection("master")
                 .enforceForAdminstrators("master", false);
    }

    private void givenBranchProtectionOverridesSignedCommits()
    {
        ovBuilder.enableBranchProtection("master")
                 .requireSignedCommits("master", false);
    }

    private void givenCollaboratorDefaults()
    {
        deBuilder.addCollaborator("default", Permission.write);
    }

    private void givenCollaboratorOverrides()
    {
        ovBuilder.addCollaborator("override", Permission.admin);
    }

    private void givenCollaboratorOverridesAreEmpty()
    {
        ovBuilder.addCollaborators(Collections.emptyMap());
    }

    private void givenCollaboratorOverridesAreNull()
    {
        ovBuilder.addCollaborators();
    }

    private void givenDefaultWorkflow()
    {
        deBuilder.addWorkflow("default");
    }

    private void givenImplictsAreOverridden()
    {
        ovBuilder.setArchived(true)
                 .setDeleteBranchOnMerge(true)
                 .setIssues(false)
                 .setMergeCommits(false)
                 .setRebaseMerge(false)
                 .setSquashMerge(false)
                 .setWiki(false);
    }

    private void givenInitializeDefault()
    {
        deBuilder.setInitialize(true);
    }

    private void givenInitializeOverride()
    {
        ovBuilder.setInitialize(false);
    }

    private void givenLabelDefaults()
    {
        deBuilder.addLabel("default", Color.from("#00000"));
    }

    private void givenLabelOverrides()
    {
        ovBuilder.addLabel("override", Color.from("#00001"));
    }

    private void givenLabelOverridesEmpty()
    {
        ovBuilder.addLabels(Collections.emptyMap());
    }

    private void givenLabelOverridesNull()
    {
        ovBuilder.addLabels();
    }

    private void givenNullOrganizations()
    {
        deBuilder.setOrganization(null);
        ovBuilder.setOrganization(null);
    }

    private void givenOverrideWorkflow()
    {
        ovBuilder.addWorkflow("override");
    }

    private void givenPrimaryBranchNoSet()
    {
        primaryBranch = "master";
        // nothing set on builders...
    }

    private void givenTeamDefaults()
    {
        deBuilder.addTeam("default", Permission.write);
    }

    private void givenTeamOverrides()
    {
        ovBuilder.addTeam("override", Permission.admin);
    }

    private void givenTeamOverridesAreEmpty()
    {
        ovBuilder.addTeams(Collections.emptyMap());
    }

    private void givenTeamOverridesAreNull()
    {
        ovBuilder.addTeams();
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
        assertThat(repository.getPlugins().size(), equalTo(3));
        assertThat(repository.getPlugins().containsKey("p1"), equalTo(true));
        assertThat(repository.getPlugins().containsKey("p2"), equalTo(true));
        assertThat(repository.getPlugins().containsKey("p3"), equalTo(true));

        JsonObject p1 = repository.getPlugin("p1");
        assertThat(p1.getBoolean("enabled"), equalTo(true));

        JsonObject p2 = repository.getPlugin("p2");
        assertThat(p2.getBoolean("enabled"), equalTo(false));

        JsonObject p3 = repository.getPlugin("p3");
        assertThat(p3.getBoolean("enabled"), equalTo(true));
        assertThat(p3.has("object"), equalTo(true));

        JsonObject object = p3.getJsonObject("object");
        assertThat(object.getInteger("k1"), equalTo(1));
        assertThat(object.getString("k2"), equalTo("v"));
    }

    private void thenDefaultWorkflowReturned()
    {
        assertThat(repository.getWorkflow(), contains(defaults.getWorkflow().toArray()));
    }

    private void thenDescriptionIsCorrect()
    {
        verifyDescription(repository, overrides);
    }

    private void thenDescriptionIsNull()
    {
        verifyDescription(repository);
    }

    private void thenEnforceForAdminsDisabled()
    {
        verifyEnforeForAdministratorsDisabled(repository, "master");
    }

    private void thenHompageIsCorrect()
    {
        verifyHomepage(overrides, repository);
    }

    private void thenHompageIsNull()
    {
        verifyHomepage(repository);
    }

    private void thenIllegalStateExceptionThrown()
    {
        assertThat(exception, notNullValue());
        assertThat(exception, instanceOf(IllegalStateException.class));
    }

    private void thenImplicitDefaultsAreCorrect()
    {
        verifyArchived(repository, false);
        verifyDeleteBranchOnMerge(repository, false);
        verifyIssues(repository, true);
        verifyMergeCommits(repository, true);
        verifyRebaseMerge(repository, true);
        verifySquashMerge(repository, true);
        verifyWiki(repository, true);
    }

    private void thenImplicitsAreOverridden()
    {
        verifyArchived(repository, true);
        verifyDeleteBranchOnMerge(repository, true);
        verifyIssues(repository, false);
        verifyMergeCommits(repository, false);
        verifyRebaseMerge(repository, false);
        verifySquashMerge(repository, false);
        verifyWiki(repository, false);
    }

    private void thenInitializationIsFalse()
    {
        verifyRepositoryNotInitialized(repository);
    }

    private void thenInitializationIsTrue()
    {
        verifyInitialized(repository, true);
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
        assertThat(repository.getPlugins().size(), equalTo(4));
        assertThat(repository.getPlugins().containsKey("p1"), equalTo(true));
        assertThat(repository.getPlugins().containsKey("p2"), equalTo(true));
        assertThat(repository.getPlugins().containsKey("p3"), equalTo(true));
        assertThat(repository.getPlugins().containsKey("p4"), equalTo(true));

        JsonObject p1 = repository.getPlugin("p1");
        assertThat(p1.getBoolean("enabled"), equalTo(true));

        JsonObject p2 = repository.getPlugin("p2");
        assertThat(p2.getBoolean("enabled"), equalTo(true));

        JsonObject p3 = repository.getPlugin("p3");
        assertThat(p3.getBoolean("enabled"), equalTo(false));
        assertThat(p3.has("object"), equalTo(true));

        JsonObject object = p3.getJsonObject("object");
        assertThat(object.getInteger("k1"), equalTo(5));
        assertThat(object.getString("k2"), equalTo("v"));
        assertThat(object.getInteger("k3"), equalTo(3));

        JsonObject p4 = repository.getPlugin("p4");
        assertThat(p4.getBoolean("enabled"), equalTo(true));
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

    private void whenBuildRepositories()
    {
        defaults = deBuilder.build();
        overrides = ovBuilder.build();

        try
        {
            repository = merger.merge(overrides, defaults);
        }
        catch (Exception e)
        {
            exception = e;
        }
    }
}
