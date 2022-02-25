package io.dangernoodle.grt.workflow.step;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import io.dangernoodle.grt.workflow.step.AbstractGithubStep;
import io.dangernoodle.grt.workflow.step.FindOrCreateRepository;


public class FindOrCreateRepositoryTest extends AbstractGithubWorkflowStepTest
{
    private boolean create = true;

    @Test
    public void testCreateOrgRepository() throws Exception
    {
        givenAnOrganization();
        givenACreatedOrgRepo();
        whenExecuteStep();
        thenOrgRepositoryIsLookedFor();
        thenOrgRepositoryIsCreated();
        thenGHRepositoryAddedToContext();
        thenStatusIsContinue();
    }

    @Test
    public void testCreateUserRepository() throws Exception
    {
        givenAUser();
        givenACreatedUserRepo();
        whenExecuteStep();
        thenUserRepositoryIsLookedFor();
        thenUserRepositoryIsCreated();
        thenGHRepositoryAddedToContext();
        thenStatusIsContinue();
    }

    @Test
    public void testFindOrgRepository() throws Exception
    {
        givenAnOrganization();
        givenAnExistingOrgRepo();
        whenExecuteStep();
        thenOrgRepositoryIsLookedFor();
        thenGHRepositoryAddedToContext();
        thenStatusIsContinue();
    }

    @Test
    public void testFindUserRepository() throws Exception
    {
        givenAUser();
        givenAnExistingUserRepo();
        whenExecuteStep();
        thenUserRepositoryIsLookedFor();
        thenGHRepositoryAddedToContext();
        thenStatusIsContinue();
    }

    @Test
    public void testFindUserRepositoryNoCreate() throws Exception
    {
        assertThrows(IllegalStateException.class, () -> {
            givenAUser();
            givenDontCreateRepository();
            whenExecuteStep();
        });
    }

    @Override
    protected AbstractGithubStep createStep()
    {
        return new FindOrCreateRepository(mockClient, create);
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

    private void givenDontCreateRepository()
    {
        create = false;
    }

    private void thenGHRepositoryAddedToContext()
    {
        verify(mockContext).add(mockGHRepository);
    }

    private void thenOrgRepositoryIsCreated() throws IOException
    {
        verify(mockClient).createOrgRepository(repository);
    }

    private void thenOrgRepositoryIsLookedFor() throws IOException
    {
        verify(mockClient).getCurrentLogin();
        verify(mockClient).getRepository(repository.getOrganization(), repository.getName());
    }

    private void thenUserRepositoryIsCreated() throws IOException
    {
        verify(mockClient).createUserRepository(repository);
    }

    private void thenUserRepositoryIsLookedFor() throws IOException
    {
        verify(mockClient).getCurrentLogin();
        verify(mockClient).getRepository(repository.getName());
    }
}
