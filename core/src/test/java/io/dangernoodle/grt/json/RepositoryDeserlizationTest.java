package io.dangernoodle.grt.json;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.jupiter.api.Test;

import io.dangernoodle.grt.Repository;
import io.dangernoodle.grt.Repository.Color;
import io.dangernoodle.grt.Repository.Settings;
import io.dangernoodle.grt.Repository.Settings.Branches;
import io.dangernoodle.grt.Repository.Settings.Branches.Protection;
import io.dangernoodle.grt.TestFiles;


public class RepositoryDeserlizationTest
{
    private String expectedName;

    private Repository repository;

    private TestFiles testFile;

    @Test
    public void testDeserialize()
    {
        givenARepositoryFile();
        whenParseIntoObject();
        thenRepositoryIsCorrect();
        thenPluginIsCorrect();
        thenWorkflowIsCorrect();
    }

    private void givenARepositoryFile()
    {
        expectedName = "grt-test-repository";
        testFile = TestFiles.mockRepository;
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

        assertThat(protection.getRequireReviews(), notNullValue());
        assertThat(protection.getRequireReviews().getRequiredReviewers(), equalTo(1));
        assertThat(protection.getRequireReviews().getDismissStaleApprovals(), equalTo(true));
        assertThat(protection.getRequireReviews().getRequireCodeOwner(), equalTo(true));
        assertThat(protection.getRequireReviews().enableRestrictDismissals(), equalTo(true));

        assertThat(protection.getRequiredChecks(), notNullValue());
        assertThat(protection.getRequiredChecks().getRequireUpToDate(), equalTo(true));
        assertThat(protection.getRequiredChecks().getContexts(), contains(repository.getName()));
    }

    private void thenPluginIsCorrect()
    {
        assertThat(repository.getPluginJson("jenkins"), notNullValue());
    }

    private void thenRepositoryIsCorrect()
    {
        assertThat(repository, notNullValue());
        assertThat(repository.getName(), equalTo(expectedName));
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

    private void thenWorkflowIsCorrect()
    {
        //assertThat(repository.getWorkflow(), notNullValue());
    }

    private void whenParseIntoObject()
    {
        repository = testFile.parseIntoObject(Repository.class);
    }
}
