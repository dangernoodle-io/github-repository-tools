package io.dangernoodle.grt.steps;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kohsuke.github.GHOrganization;
import org.kohsuke.github.GHTeam;
import org.kohsuke.github.GHUser;
import org.mockito.Mock;

import io.dangernoodle.grt.Repository.Settings.Permission;
import io.dangernoodle.grt.internal.GithubWorkflow;


public class AddTeamsAndCollaboratorsTest extends AbstractGithubWorkflowStepTest
{
    @Mock
    private GHTeam mockGHTeam;

    @Mock
    private GHUser mockGHUser;

    @Mock
    private GHUser mockOwner;

    @Override
    @BeforeEach
    public void beforeEach() throws Exception
    {
        super.beforeEach();

        when(mockGHRepository.getOwner()).thenReturn(mockOwner);
    }

    @Test
    public void testAddCollaborator() throws Exception
    {
        givenAUserRepo();
        givenACollaborator();
        whenExecuteStep();
        thenCollaboratorIsAdded();
        thenStatusIsContinue();
    }

    @Test
    public void testAddNonExistantTeamToOrg() throws Exception
    {
        givenAnOrgRepo();
        givenATeamThatDoesntExist();
        whenExecuteStep();
        thenTeamIsNotAdded();
        thenStatusIsContinue();
    }

    @Test
    public void testAddNonExistantUser() throws Exception
    {
        givenAUserRepo();
        givenACollaboratorThatDoesNotExist();
        whenExecuteStep();
        thenUserIsNotAdded();
        thenStatusIsContinue();
    }

    @Test
    public void testAddTeamAndCollaboratorToOrg() throws Exception
    {
        givenAnOrgRepo();
        givenATeam();
        givenACollaborator();
        whenExecuteStep();
        thenTeamIsAdded();
        thenCollaboratorIsAdded();
        thenStatusIsContinue();
    }

    @Override
    protected GithubWorkflow.Step createStep()
    {
        return new AddTeamsAndCollaborators(mockClient);
    }

    private void givenACollaborator() throws IOException
    {
        repoBuilder.addCollaborator("user", Permission.write);
        when(mockClient.getUser("user")).thenReturn(mockGHUser);
    }

    private void givenACollaboratorThatDoesNotExist() throws IOException
    {
        repoBuilder.addCollaborator("doesnotexist", Permission.write);
        when(mockClient.getUser("doesnotexist")).thenReturn(null);
    }

    private void givenAnOrgRepo() throws IOException
    {
        repoBuilder.setOrganization("org");
        when(mockOwner.getType()).thenReturn("Organization");
    }

    private void givenATeam() throws IOException
    {
        repoBuilder.addTeam("team", Permission.read);
        when(mockClient.getTeam("org", "team")).thenReturn(mockGHTeam);
    }

    private void givenATeamThatDoesntExist() throws IOException
    {
        repoBuilder.addTeam("doesnotexist", Permission.write);
        when(mockClient.getTeam("org", "doesnotexist")).thenReturn(null);
    }

    private void givenAUserRepo()
    {
        repoBuilder.setOrganization("user");
    }

    private void thenCollaboratorIsAdded() throws IOException
    {
        verify(mockGHRepository).addCollaborators(mockGHUser);
    }

    private void thenTeamIsAdded() throws IOException
    {
        verify(mockGHTeam).add(mockGHRepository, GHOrganization.Permission.PULL);
    }

    private void thenTeamIsNotAdded() throws IOException
    {
        verify(mockGHTeam, times(0)).add(eq(mockGHRepository), any());
    }

    private void thenUserIsNotAdded() throws IOException
    {
        verify(mockGHRepository, times(0)).addCollaborators(mockGHUser);
    }
}
