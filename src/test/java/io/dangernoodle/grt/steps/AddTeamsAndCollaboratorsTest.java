package io.dangernoodle.grt.steps;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kohsuke.github.GHOrganization;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHTeam;
import org.kohsuke.github.GHUser;
import org.mockito.Mock;

import io.dangernoodle.grt.Repository.Permission;
import io.dangernoodle.grt.internal.GithubWorkflow;


public class AddTeamsAndCollaboratorsTest extends AbstractGithubWorkflowStepTest
{
    @Mock
    private GHTeam mockGHTeam;

    @Mock
    private GHUser mockGHUser;

    @Override
    @BeforeEach
    public void beforeEach() throws Exception
    {
        super.beforeEach();
        when(mockContext.get(GHRepository.class)).thenReturn(mockGHRepository);
    }

    @Test
    public void testAddCollaborator() throws Exception
    {
        givenAUserRepo();
        givenACollaborator();
        whenExecuteStep();
        thenCollaboratorIsAdded();
    }

    @Test
    public void testAddNonExistantTeamToOrg() throws Exception
    {
        givenAnOrgRepo();
        givenATeamThatDoesntExist();
        whenExecuteStep();
        thenTeamIsNotAdded();
    }

    @Test
    public void testAddNonExistantUser() throws Exception
    {
        givenAUserRepo();
        givenACollaboratorThatDoesNotExist();
        whenExecuteStep();
        thenUserIsNotAdded();
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

    private void givenAnOrgRepo()
    {
        repoBuilder.setOrganization("org");
        when(mockContext.isOrg()).thenReturn(true);
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

    private void thenTeamIsNotAdded()
    {
        verifyZeroInteractions(mockGHTeam);
    }

    private void thenUserIsNotAdded()
    {
        verifyZeroInteractions(mockGHRepository);
    }
}
