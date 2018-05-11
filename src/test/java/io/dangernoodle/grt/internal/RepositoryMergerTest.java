package io.dangernoodle.grt.internal;

import static io.dangernoodle.TestAsserts.verifyCommitsAdminsDisabled;
import static io.dangernoodle.TestAsserts.verifyRequireReviewsDisabled;
import static io.dangernoodle.TestAsserts.verifyRequiredStatusChecksDisabled;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.dangernoodle.grt.Repository;
import io.dangernoodle.grt.Repository.Color;
import io.dangernoodle.grt.Repository.Settings.Branches.Protection;


public class RepositoryMergerTest
{
    private RepositoryBuilder dBuilder;

    private Repository defaults;

    private RepositoryBuilder oBuilder;

    private Repository overrides;

    private Repository repository;

    @BeforeEach
    public void setup()
    {
        dBuilder = new RepositoryBuilder();
        oBuilder = new RepositoryBuilder();

        // this is a required value, so just set a default here
        dBuilder.setOrganization("default-org");
    }

    @Test
    public void testGetDefaultLabels()
    {
        givenDefaultLabels();
        whenBuildRepositories();
        thenDefaultLabelsReturned();
    }

    @Test
    public void testGetDefaultOrganization()
    {
        // see #setup()
        whenBuildRepositories();
        thenDefaultOrganizationReturned();
    }

    @Test
    public void testGetMergedLabels()
    {
        givenDefaultLabels();
        givenMergedLabels();
        whenBuildRepositories();
        thenDefaultLabelsReturned();
        thenOverrideLabelsReturned();

        // overriding 'default' label(s) as part of a 'merge' is allowed
        givenOverrideLabels();
        whenBuildRepositories();
        thenDefaultLabelsReturned();
        thenOverrideLabelsReturned();
        thenOverrideColorIsCorrect();
    }

    @Test
    public void testGetMergedPlugin()
    {
        givenADefaultPlugin();
        givenAMergedPlugin();
        whenBuildRepositories();
        thenDefaultPluginReturned();
        thenMergedPluginReturned();

        // overriding 'default' plugin(s) as part of a 'merge' is allowed
        givenAnOverridenPlugin();
        whenBuildRepositories();
        thenOverridenPluginIsReturned();
    }

    @Test
    public void testGetMergedPushTeams()
    {
        givenDefaultPushTeams();
        givenOverridePushTeams();
        whenBuildRepositories();
        thenDefaultPushTeamsReturned();
        thenOverridePushTeamsReturned();
    }

    @Test
    public void testGetMergedRequiredCheckContexts()
    {
        givenDefaultStatusChecks();
        givenOverrideStatusChecks();
        whenBuildRepositories();
        thenDefaultStatusChecksReturned();
        thenOverrideStatusCheckesReturned();
    }

    @Test
    public void testGetOverrideOrganization()
    {
        givenAnOverrideOrganization();
        whenBuildRepositories();
        thenOverrideOrganizationReturned();
    }

    @Test
    public void testGetWorkflow()
    {
        givenDefaultWorkflow();
        whenBuildRepositories();
        thenDefaultWorkflowReturned();

        givenOverrideWorkflow();
        whenBuildRepositories();
        thenOverrideWorkflowReturned();
    }

    @Test
    public void testPrimaryBranchDefaultsToMaster()
    {
        whenBuildRepositories();
        thenPrimaryBranchIsMaster();
    }

    @Test
    public void testRepositoryDefaults()
    {
        whenBuildRepositories();
        whenBuildRepositories();
        thenRepositoryDefaultsAreReturned();
    }

    @Test
    public void testRequiredCheckContextsDefaultOnly()
    {
        givenDefaultStatusChecks();
        whenBuildRepositories();
        thenDefaultStatusChecksReturned();
    }

    @Test
    public void testRequiredCheckContextsOverrideOnly()
    {
        givenOverrideStatusChecks();
        whenBuildRepositories();
        thenOverrideStatusCheckesReturned();
    }

    private void assertPluginValues(String key, String json)
    {
        assertThat(repository.getPlugins().get(key), notNullValue());
        assertThat(repository.getPlugins().get(key), equalTo(json));
    }

    private Protection getProtection(Repository repository, String branch)
    {
        return repository.getSettings().getBranches().getProtection(branch);
    }

    private void givenADefaultPlugin()
    {
        dBuilder.addPlugin("default", "{\"default\": \"true\"}");
    }

    private void givenAMergedPlugin()
    {
        dBuilder.addPlugin("merged", "{\"merged\": \"true\"}");
    }

    private void givenAnOverridenPlugin()
    {
        dBuilder.addPlugin("default", "{\"default\": \"false\"}");
    }

    private void givenAnOverrideOrganization()
    {
        oBuilder.setOrganization("override-org");
    }

    private void givenDefaultLabels()
    {
        dBuilder.addLabel("default", Color.from("#00000"));
    }

    private void givenDefaultPushTeams()
    {
        dBuilder.addTeamPushAccess("master", "default");
    }

    private void givenDefaultStatusChecks()
    {
        dBuilder.addRequiredContext("master", "default");
    }

    private void givenDefaultWorkflow()
    {
        dBuilder.addWorkflow("default");
    }

    private void givenMergedLabels()
    {
        oBuilder.addLabel("merged", Color.from("#00001"));
    }

    private void givenOverrideLabels()
    {
        oBuilder.addLabel("default", Color.from("#00001"));
    }

    private void givenOverridePushTeams()
    {
        oBuilder.addTeamPushAccess("master", "override");
    }

    private void givenOverrideStatusChecks()
    {
        oBuilder.addRequiredContext("master", "override");
    }

    private void givenOverrideWorkflow()
    {
        oBuilder.addWorkflow("override");
    }

    private void thenDefaultLabelsReturned()
    {
        assertThat(repository.getSettings().getLabels(), notNullValue());
        assertThat(repository.getSettings().getLabels().keySet().containsAll(defaults.getSettings().getLabels().keySet()),
                equalTo(true));
    }

    private void thenDefaultOrganizationReturned()
    {
        assertThat(repository.getOrganization(), equalTo(defaults.getOrganization()));
    }

    private void thenDefaultPluginReturned()
    {
        assertPluginValues("default", "{\"default\":\"true\"}");
    }

    private void thenDefaultPushTeamsReturned()
    {
        Protection dProtection = getProtection(defaults, "master");
        Protection rProtection = getProtection(repository, "master");

        assertThat(rProtection, notNullValue());
        assertThat(rProtection.getPushTeams().containsAll(dProtection.getPushTeams()), equalTo(true));
    }

    private void thenDefaultStatusChecksReturned()
    {
        Protection dProtection = getProtection(defaults, "master");
        Protection rProtection = getProtection(repository, "master");

        assertThat(rProtection, notNullValue());
        assertThat(rProtection.getRequiredChecks().getContexts().containsAll(dProtection.getRequiredChecks().getContexts()),
                equalTo(true));
    }

    private void thenDefaultWorkflowReturned()
    {
        assertThat(repository.getWorkflow().containsAll(defaults.getWorkflow()), equalTo(true));
    }

    private void thenMergedPluginReturned()
    {
        assertPluginValues("merged", "{\"merged\":\"true\"}");
    }

    private void thenOverrideColorIsCorrect()
    {
        assertThat(repository.getSettings().getLabels().get("default"),
                equalTo(overrides.getSettings().getLabels().get("default")));
    }

    private void thenOverrideLabelsReturned()
    {
        assertThat(repository.getSettings().getLabels(), notNullValue());
        assertThat(repository.getSettings().getLabels().keySet().containsAll(overrides.getSettings().getLabels().keySet()),
                equalTo(true));
    }

    private void thenOverridenPluginIsReturned()
    {
        assertPluginValues("default", "{\"default\":\"false\"}");
    }

    private void thenOverrideOrganizationReturned()
    {
        assertThat(repository.getOrganization(), equalTo(overrides.getOrganization()));
    }

    private void thenOverridePushTeamsReturned()
    {
        Protection oProtection = getProtection(overrides, "master");
        Protection rProtection = getProtection(repository, "master");

        assertThat(rProtection, notNullValue());
        assertThat(rProtection.getPushTeams().containsAll(oProtection.getPushTeams()), equalTo(true));
    }

    private void thenOverrideStatusCheckesReturned()
    {
        Protection oProtection = getProtection(overrides, "master");
        Protection rProtection = getProtection(repository, "master");

        assertThat(rProtection, notNullValue());
        assertThat(rProtection.getRequiredChecks().getContexts().containsAll(oProtection.getRequiredChecks().getContexts()),
                equalTo(true));
    }

    private void thenOverrideWorkflowReturned()
    {
        assertThat(repository.getWorkflow().containsAll(overrides.getWorkflow()), equalTo(true));
        assertThat(repository.getWorkflow().containsAll(defaults.getWorkflow()), equalTo(false));
    }

    private void thenPrimaryBranchIsMaster()
    {
        assertThat(repository.getSettings().getBranches().getDefault(), equalTo("master"));
    }

    private void thenRepositoryDefaultsAreReturned()
    {
        Protection protection = getProtection(repository, "master");

        verifyCommitsAdminsDisabled(protection);
        verifyRequireReviewsDisabled(protection);
        verifyRequiredStatusChecksDisabled(protection);
    }

    private void whenBuildRepositories()
    {
        defaults = dBuilder.build();
        overrides = oBuilder.build();

        repository = new RepositoryMerger(defaults, overrides).merge();
    }
}
