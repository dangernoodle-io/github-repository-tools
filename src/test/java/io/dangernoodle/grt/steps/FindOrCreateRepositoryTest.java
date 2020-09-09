package io.dangernoodle.grt.steps;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import io.dangernoodle.grt.internal.GithubWorkflow;


public class FindOrCreateRepositoryTest extends AbstractGithubWorkflowStepTest
{
    @Test
    public void testCreateOrgRepository() throws Exception
    {
        givenAnOrganization();
        givenACreatedOrgRepo();
        whenExecuteStep();
        thenOrgRepositoryIsCreated();
        thenGHRepositoryAddedToContext();
    }

    @Test
    public void testCreateUserRepository() throws Exception
    {
        givenAUser();
        givenACreatedUserRepo();
        whenExecuteStep();
        thenUserRepositoryIsCreated();
        thenGHRepositoryAddedToContext();
    }

    @Test
    public void testFindOrgRepository() throws Exception
    {
        givenAnOrganization();
        givenAnExistingOrgRepo();
        whenExecuteStep();
        thenOrgRepositoryIsFound();
        thenGHRepositoryAddedToContext();
    }

    @Test
    public void testFindUserRepository() throws Exception
    {
        givenAUser();
        givenAnExistingUserRepo();
        whenExecuteStep();
        thenUserRepositoryIsFound();
        thenGHRepositoryAddedToContext();
    }

    @Override
    protected GithubWorkflow.Step createStep()
    {
        return new FindOrCreateRepository(mockClient);
    }

    private void givenACreatedOrgRepo() throws IOException
    {
        when(mockClient.createOrgRepository(any())).thenReturn(mockGHRepository);
    }

    private void givenACreatedUserRepo() throws IOException
    {
        when(mockClient.createUserRepository(any())).thenReturn(mockGHRepository);
    }

    private void givenAnExistingOrgRepo() throws IOException
    {
        when(mockClient.getRepository(any(), any())).thenReturn(mockGHRepository);
    }

    private void givenAnExistingUserRepo() throws IOException
    {
        when(mockClient.getRepository(any())).thenReturn(mockGHRepository);
    }

    private void givenAnOrganization()
    {
        repoBuilder.setOrganization("organization");
    }

    private void givenAUser() throws IOException
    {
        repoBuilder.setOrganization("user");
        when(mockClient.getCurrentLogin()).thenReturn("user");
    }

    private void thenGHRepositoryAddedToContext()
    {
        verify(mockContext).add(mockGHRepository);
    }

    private void thenOrgRepositoryIsCreated() throws IOException
    {
        verify(mockClient).getRepository(repository.getOrganization(), repository.getName());
        verify(mockClient).createOrgRepository(repository);
    }

    private void thenOrgRepositoryIsFound() throws IOException
    {
        verify(mockClient).getRepository(repository.getOrganization(), repository.getName());
        verify(mockClient, times(0)).createOrgRepository(repository);
    }

    private void thenUserRepositoryIsCreated() throws IOException
    {
        verify(mockClient).getRepository(repository.getName());
        verify(mockClient).createUserRepository(repository);
    }

    private void thenUserRepositoryIsFound() throws IOException
    {
        verify(mockClient).getRepository(repository.getName());
        verify(mockClient, times(0)).createUserRepository(repository);
    }
}
