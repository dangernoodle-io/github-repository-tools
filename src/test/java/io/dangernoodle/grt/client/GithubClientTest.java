package io.dangernoodle.grt.client;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kohsuke.github.GHCreateRepositoryBuilder;
import org.kohsuke.github.GHMyself;
import org.kohsuke.github.GHOrganization;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.dangernoodle.grt.Repository;
import io.dangernoodle.grt.Repository.Settings;
import io.dangernoodle.grt.repository.RepositoryBuilder;
import io.dangernoodle.grt.util.JsonTransformer;


public class GithubClientTest
{
    private GithubClient client;

    private GHOrganization ghOrg;

    private GHRepository ghRepo;

    @Mock
    private GitHub mockGithub;

    @Mock
    private GHMyself mockMyself;

    @Mock
    private GHOrganization mockOrg;

    @Mock
    private GHRepository mockRepo;

    @Mock
    private GHCreateRepositoryBuilder mockRepoBuilder;

    @Mock
    private GHUser mockUser;

    private RepositoryBuilder repoBuilder;

    private Repository repository;
    
    @BeforeEach
    public void beforeEach() throws Exception
    {
        MockitoAnnotations.initMocks(this);
        when(mockRepoBuilder.create()).thenReturn(mockRepo);

        repoBuilder = createBuilder();
        client = new GithubClient(mockGithub, mockUser);
    }

    @Test
    public void testCreateOrgRepository() throws Exception
    {
        givenAnOrgRepository();
        whenCreateOrgRepository();
        thenOrgCreateRepositoryInvoked();
        thenVerifyRepositoryBuilder();
        thenRepositoryIsReturned();
    }

    @Test
    public void testCreateUserRepository() throws Exception
    {
        givenAUserRepository();
        whenCreateUserRepository();
        thenUserCreateRepositoryInvoked();
        thenVerifyRepositoryBuilder();
        thenRepositoryIsReturned();
    }

    @Test
    public void testGetOrganization() throws Exception
    {
        givenAnOrganization();
        whenGetOrganization();
        thenOrganizationIsReturned();
        thenGithubOrganizationInvoked();

        whenGetOrganization();
        thenOrganizationIsReturned();
        thenGithubOrganizationNotInvoked();
    }

    private RepositoryBuilder createBuilder()
    {
        RepositoryBuilder repoBuilder = new RepositoryBuilder(new JsonTransformer());
        repoBuilder.setName("repository")
                   .setInitialize(true)
                   .setIgnoreTemplate("Java")
                   .setLicenseTemplate("mit")
                   .setPrivate(true);

        return repoBuilder;
    }

    private void givenAnOrganization() throws IOException
    {
        when(client.getOrganization("organization")).thenReturn(mockOrg);
    }

    private void givenAnOrgRepository() throws IOException
    {
        repoBuilder.setOrganization("organization");
        when(mockGithub.getOrganization("organization")).thenReturn(mockOrg);
        when(mockOrg.createRepository("repository")).thenReturn(mockRepoBuilder);
    }

    private void givenAUserRepository()
    {
        repoBuilder.setOrganization("username");
        when(mockGithub.createRepository("repository")).thenReturn(mockRepoBuilder);
    }

    private void thenGithubOrganizationInvoked() throws IOException
    {
        verify(mockGithub).getOrganization(any());
    }

    private void thenGithubOrganizationNotInvoked()
    {
        verifyNoMoreInteractions(mockGithub);
    }

    private void thenOrganizationIsReturned()
    {
        assertThat(ghOrg, equalTo(mockOrg));
    }

    private void thenOrgCreateRepositoryInvoked() throws IOException
    {
        verify(mockGithub).getOrganization(repository.getOrganization());
        verify(mockOrg).createRepository(repository.getName());
    }

    private void thenRepositoryIsReturned()
    {
        assertThat(ghRepo, equalTo(mockRepo));
    }

    private void thenUserCreateRepositoryInvoked()
    {
        verify(mockGithub).createRepository(repository.getName());
    }

    private void thenVerifyRepositoryBuilder() throws IOException
    {
        Settings settings = repository.getSettings();

        verify(mockRepoBuilder).autoInit(settings.autoInitialize());
        verify(mockRepoBuilder).gitignoreTemplate(repository.getIgnoreTemplate());
        verify(mockRepoBuilder).licenseTemplate(repository.getLicenseTemplate());
    }

    private void whenCreateOrgRepository() throws IOException
    {
        repository = repoBuilder.build();
        ghRepo = client.createOrgRepository(repository);
    }

    private void whenCreateUserRepository() throws IOException
    {
        repository = repoBuilder.build();
        ghRepo = client.createUserRepository(repository);
    }

    private void whenGetOrganization() throws IOException
    {
        ghOrg = client.getOrganization("organization");
    }
}
