package grt;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import java.io.IOException;
import java.io.UncheckedIOException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.kohsuke.github.GHLabel;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

import io.dangernoodle.grt.GithubClient;
import io.dangernoodle.grt.Repository;
import io.dangernoodle.grt.Repository.Color;
import io.dangernoodle.grt.Workflow;
import io.dangernoodle.grt.internal.GithubWorkflow;
import io.dangernoodle.grt.internal.RepositoryBuilder;


public class UserRepositoryIT
{
    @RegisterExtension
    protected static final GitHub github = createGitHub();

    protected RepositoryBuilder builder;

    protected Workflow.Context context;

    protected GHRepository ghRepo;

    protected Repository repository;

    protected GithubWorkflow workflow;

    @AfterEach
    public void afterEach() throws Exception
    {
        if (ghRepo != null)
        {
            ghRepo.delete();
        }
    }

    @BeforeEach
    public void beforeEach() throws Exception
    {
        builder = new RepositoryBuilder();
        context = new Workflow.Context();
        workflow = new GithubWorkflow(new GithubClient(github));

        builder.setName("github-repository-tools-test")
               .setOrganization(getOrganization())
               .setInitialize(true);
    }

    @Test
    public void testCreateLabels() throws Exception
    {
        givenARepositoryWithLabels();
        whenExecuteWorkflow();
        thenRepositoryWasCreated();
        thenLabelsWereConfigured();
    }

    protected String getOrganization() throws IOException
    {
        return github.getMyself().getLogin();
    }

    private void givenARepositoryWithLabels() throws IOException
    {
        builder.addLabel("skip-build", Color.from("#006b75"));
    }

    private void thenLabelsWereConfigured() throws IOException
    {
        GHLabel label = ghRepo.getLabel("skip-build");
        assertThat(label.getColor(), equalTo("006b75"));
    }

    private void thenRepositoryWasCreated() throws IOException
    {
        assertThat(ghRepo, notNullValue());
        assertThat(ghRepo.getName(), equalTo(repository.getName()));
        assertThat(ghRepo.getOwnerName(), equalTo(getOrganization()));
    }

    private void whenExecuteWorkflow() throws IOException
    {
        repository = builder.build();
        workflow.execute(repository, context);

        ghRepo = context.get(GHRepository.class);
    }

    private static GitHub createGitHub()
    {
        try
        {
            GitHub github = GitHubBuilder.fromCredentials().build();
            github.checkApiUrlValidity();

            return github;
        }
        catch (IOException e)
        {
            throw new UncheckedIOException(e);
        }
    }
}
