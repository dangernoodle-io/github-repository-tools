package io.dangernoodle.grt.json;

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
        verifyRequiredStatusChecksEnabled(protection);
    }

    private void verifyRequireReviewsEnabled(Protection protection)
    {
        assertThat(protection.getRequireReviews(), notNullValue());
        assertThat(protection.getRequireReviews().isEnabled(), equalTo(true));
        assertThat(protection.getRequireReviews().getRequiredReviewers(), equalTo(2));
        assertThat(protection.getRequireReviews().getDismissStaleApprovals(), equalTo(true));
        assertThat(protection.getRequireReviews().getRequireCodeOwner(), equalTo(true));
        assertThat(protection.getRequireReviews().enableRestrictDismissals(), equalTo(true));
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
        verifyRequiredStatusChecksEnabled(protection);
    }

    private void thenWorkflowIsCorrect()
    {
        assertThat(repository.getWorkflow(), notNullValue());
    }

    private void verifyCommitsAdminsDisabled(Protection protection)
    {
        assertThat(protection.getRequireSignedCommits(), equalTo(false));
        assertThat(protection.getIncludeAdministrators(), equalTo(false));
    }
    
    private void verifyRequiredStatusChecksDisabled(Protection protection)
    {
        assertThat(protection.getRequiredChecks(), notNullValue());
        assertThat(protection.getRequiredChecks().isEnabled(), equalTo(false));
        assertThat(protection.getRequiredChecks().getRequireUpToDate(), equalTo(false));
        assertThat(protection.getRequiredChecks().getContexts().isEmpty(), equalTo(true));
    }
    
    private void verifyRequiredStatusChecksEnabled(Protection protection)
    {
        assertThat(protection.getRequiredChecks(), notNullValue());
        assertThat(protection.getRequiredChecks().isEnabled(), equalTo(true));
        assertThat(protection.getRequiredChecks().getRequireUpToDate(), equalTo(true));
        assertThat(protection.getRequiredChecks().getContexts(), contains(repository.getName()));
    }
    
    private void verifyRequireReviewsDisabled(Protection protection)
    {
        assertThat(protection.getRequireReviews(), notNullValue());
        assertThat(protection.getRequireReviews().isEnabled(), equalTo(false));
        assertThat(protection.getRequireReviews().getRequiredReviewers(), equalTo(1));
        assertThat(protection.getRequireReviews().getDismissStaleApprovals(), equalTo(false));
        assertThat(protection.getRequireReviews().getRequireCodeOwner(), equalTo(false));
        assertThat(protection.getRequireReviews().enableRestrictDismissals(), equalTo(false));
    }

    private void whenParseIntoObject()
    {
        repository = testFile.parseIntoObject(Repository.class);
    }
}
