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
    public void testDescriptionAlreadySet() throws Exception
    {
        givenADescription();
        givenAnExistingOrgRepo();
        givenAnExistingDescription();
        whenExecuteStep();
        thenDescriptionIsNotChanged();
    }

    @Test
    public void testDescriptionNull() throws Exception
    {
        whenExecuteStep();
        thenDescriptionIsNotChanged();
    }

    @Test
    public void testDescriptionSet() throws Exception
    {
        givenADescription();
        givenAnExistingOrgRepo();
        whenExecuteStep();
        thenDescriptionIsSet();
    }

    @Test
    public void testDescriptionTheSame() throws Exception
    {
        givenADescription();
        givenAnExistingOrgRepo();
        givenTheSameDescription();
        whenExecuteStep();
        thenDescriptionIsNotChanged();
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

    @Test
    public void testHomepageAlreadySet() throws Exception
    {
        givenAHomepage();
        givenAnExistingOrgRepo();
        givenAnExistingHomepage();
        whenExecuteStep();
        thenHomepageIsNotChanged();
    }

    @Test
    public void testHomepageNull() throws Exception
    {
        whenExecuteStep();
        thenHomepageIsNotChanged();
    }

    @Test
    public void testHomepageSet() throws Exception
    {
        givenAHomepage();
        givenAnExistingOrgRepo();
        whenExecuteStep();
        thenHomepageIsSet();
    }

    @Test
    public void testHomepageTheSame() throws Exception
    {
        givenAHomepage();
        givenAnExistingOrgRepo();
        givenTheSameHomepage();
        whenExecuteStep();
        thenHomepageIsNotChanged();
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

    private void givenADescription()
    {
        repoBuilder.setDescription("description");
    }

    private void givenAHomepage()
    {
        repoBuilder.setHomepage("homepage");
    }

    private void givenAnExistingDescription()
    {
        when(mockGHRepository.getDescription()).thenReturn("existing");
    }

    private void givenAnExistingHomepage()
    {
        when(mockGHRepository.getHomepage()).thenReturn("existing");
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

    private void givenTheSameDescription()
    {
        when(mockGHRepository.getDescription()).thenReturn("description");
    }

    private void givenTheSameHomepage()
    {
        when(mockGHRepository.getHomepage()).thenReturn("homepage");
    }

    private void thenContextIndicatesOrganization()
    {
        verify(mockContext).setOrg(true);
    }

    private void thenContextIndicatesUser()
    {
        verify(mockContext).setOrg(false);
    }

    private void thenDescriptionIsNotChanged() throws IOException
    {
        verify(mockGHRepository, times(0)).setDescription(repository.getDescription());
    }

    private void thenDescriptionIsSet() throws IOException
    {
        verify(mockGHRepository).setDescription(repository.getDescription());
    }

    private void thenGHRepositoryAddedToContext()
    {
        verify(mockContext).add(mockGHRepository);
    }

    private void thenHomepageIsNotChanged() throws IOException
    {
        verify(mockGHRepository, times(0)).setHomepage(repository.getHomepage());
    }

    private void thenHomepageIsSet() throws IOException
    {
        verify(mockGHRepository).setHomepage(repository.getHomepage());
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
