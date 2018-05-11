package io.dangernoodle.grt.json;

import static io.dangernoodle.TestAsserts.verifyCommitsAdminsDisabled;
import static io.dangernoodle.TestAsserts.verifyRequireReviewsDisabled;
import static io.dangernoodle.TestAsserts.verifyRequireReviewsEnabled;
import static io.dangernoodle.TestAsserts.verifyRequiredStatusChecksDisabled;
import static io.dangernoodle.TestAsserts.verifyRequiredStatusChecksEnabled;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.jupiter.api.Test;

import io.dangernoodle.TestFiles;
import io.dangernoodle.grt.Repository;
import io.dangernoodle.grt.Repository.Color;
import io.dangernoodle.grt.Repository.Settings;
import io.dangernoodle.grt.Repository.Settings.Branches;
import io.dangernoodle.grt.Repository.Settings.Branches.Protection;


public class RepositoryDeserlizationTest
{
    private Repository repository;

    private TestFiles testFile;

    @Test
    public void testBranchProtection()
    {
        givenABranchProtection();
        whenParseIntoObject();
        thenBranchProtectionIsCorrect();
    }

    @Test
    public void testRepository()
    {
        givenARepository();
        whenParseIntoObject();
        thenRepositoryIsCorrect();
        thenPluginIsCorrect();
        thenWorkflowIsCorrect();
    }

    @Test
    public void testRequireStatusChecks()
    {
        givenRequiredStatusChecks();
        whenParseIntoObject();
        thenRequiredStatusChecksAreCorrect();
    }

    private void givenABranchProtection()
    {
        testFile = TestFiles.branchProtection;
    }

    private void givenARepository()
    {
        testFile = TestFiles.mockRepository;
    }

    private void givenRequiredStatusChecks()
    {
        testFile = TestFiles.requireStatusChecks;
    }

    private void thenBranchProtectionIsCorrect()
    {
        Branches branches = repository.getSettings().getBranches();

        assertThat(branches, notNullValue());
        assertThat(branches.getDefault(), equalTo("master"));

        assertThat(branches.getProtection("master"), notNullValue());
        Protection protection = branches.getProtection("master");

        verifyCommitsAdminsDisabled(protection);
        verifyRequireReviewsDisabled(protection);
        verifyRequiredStatusChecksDisabled(protection);
    }

    private void thenCompleteBranchesIsCorrect()
    {
        Branches branches = repository.getSettings().getBranches();
        assertThat(branches, notNullValue());

        assertThat(branches.getDefault(), equalTo("master"));
        assertThat(branches.getOther().isEmpty(), equalTo(false));
        assertThat(branches.getOther(), contains("other"));

        assertThat(branches.getProtection("master"), notNullValue());
        Protection protection = branches.getProtection("master");

        assertThat(protection.getRequireSignedCommits(), equalTo(true));
        assertThat(protection.getIncludeAdministrators(), equalTo(true));

        verifyRequireReviewsEnabled(protection);
        verifyRequiredStatusChecksEnabled(protection, repository.getName());
    }

    private void thenPluginIsCorrect()
    {
        assertThat(repository.getPlugins().get("jenkins"), notNullValue());
        assertThat(repository.getPlugins().get("jenkins"), equalTo("[{\"container\":\"maven\"}]"));
    }

    private void thenRepositoryIsCorrect()
    {
        assertThat(repository, notNullValue());
        assertThat(repository.getName(), equalTo("grt-test-repository"));
        assertThat(repository.getOrganization(), equalTo("dangernoodle-io"));

        assertThat(repository.getSettings(), notNullValue());
        Settings settings = repository.getSettings();

        assertThat(settings.autoInitialize(), equalTo(true));
        assertThat(settings.isPrivate(), equalTo(true));

        assertThat(settings.getLabels().isEmpty(), equalTo(false));
        assertThat(settings.getLabels().get("label"), equalTo(Color.from("#00000")));

        assertThat(settings.getTeams().isEmpty(), equalTo(false));
        // TODO: validate actual teams/permissions

        assertThat(settings.getCollaborators().isEmpty(), equalTo(false));
        // TODO: validate actual teams/permissions

        thenCompleteBranchesIsCorrect();
    }

    private void thenRequiredStatusChecksAreCorrect()
    {
        Branches branches = repository.getSettings().getBranches();

        assertThat(branches, notNullValue());
        assertThat(branches.getDefault(), equalTo("master"));

        assertThat(branches.getProtection("master"), notNullValue());
        Protection protection = branches.getProtection("master");

        verifyCommitsAdminsDisabled(protection);
        verifyRequireReviewsDisabled(protection);
        verifyRequiredStatusChecksEnabled(protection, repository.getName());
    }

    private void thenWorkflowIsCorrect()
    {
        assertThat(repository.getWorkflow(), notNullValue());
    }

    private void whenParseIntoObject()
    {
        repository = testFile.parseIntoObject(Repository.class);
    }
}
