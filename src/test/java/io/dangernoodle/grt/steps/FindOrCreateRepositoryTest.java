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
        thenContextIndicatesOrganization();
    }

    @Test
    public void testCreateUserRepository() throws Exception
    {
        givenAUser();
        givenACreatedUserRepo();
        whenExecuteStep();
        thenUserRepositoryIsCreated();
        thenGHRepositoryAddedToContext();
        thenContextIndicatesUser();
    }

    @Test
    public void testFindOrgRepository() throws Exception
    {
        givenAnOrganization();
        givenAnExistingOrgRepo();
        whenExecuteStep();
        thenOrgRepositoryIsFound();
        thenGHRepositoryAddedToContext();
        thenContextIndicatesOrganization();
    }

    @Test
    public void testFindUserRepository() throws Exception
    {
        givenAUser();
        givenAnExistingUserRepo();
        whenExecuteStep();
        thenUserRepositoryIsFound();
        thenGHRepositoryAddedToContext();
        thenContextIndicatesUser();
    }

    @Override
    protected GithubWorkflow.Step createStep()
    {
        return new FindOrCreateRepository(mockClient);
    }

    private void givenACreatedOrgRepo() throws IOException
    {
        when(mockClient.createRepository(any(), any(), any())).thenReturn(mockGHRepository);
    }

    private void givenACreatedUserRepo() throws IOException
    {
        when(mockClient.createRepository(any(), any())).thenReturn(mockGHRepository);
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

    private void thenContextIndicatesOrganization()
    {
        verify(mockContext).setOrg(true);
    }

    private void thenContextIndicatesUser()
    {
        verify(mockContext).setOrg(false);
    }

    private void thenGHRepositoryAddedToContext()
    {
        verify(mockContext).add(mockGHRepository);
    }

    private void thenOrgRepositoryIsCreated() throws IOException
    {
        verify(mockClient).getRepository(repository.getOrganization(), repository.getName());
        verify(mockClient).createRepository(repository.getName(), repository.getOrganization(), repository.getSettings());
    }

    private void thenOrgRepositoryIsFound() throws IOException
    {
        verify(mockClient).getRepository(repository.getOrganization(), repository.getName());
        verify(mockClient, times(0)).createRepository(repository.getName(), repository.getOrganization(), repository.getSettings());
    }

    private void thenUserRepositoryIsCreated() throws IOException
    {
        verify(mockClient).getRepository(repository.getName());
        verify(mockClient).createRepository(repository.getName(), repository.getSettings());
    }

    private void thenUserRepositoryIsFound() throws IOException
    {
        verify(mockClient).getRepository(repository.getName());
        verify(mockClient, times(0)).createRepository(repository.getName(), repository.getSettings());
    }
}
