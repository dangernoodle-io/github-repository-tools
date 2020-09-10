package grt;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.io.IOException;
import java.io.UncheckedIOException;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kohsuke.github.GHBranchProtection;
import org.kohsuke.github.GHLabel;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

import io.dangernoodle.grt.GithubClient;
import io.dangernoodle.grt.Repository;
import io.dangernoodle.grt.Repository.Settings.Color;
import io.dangernoodle.grt.ext.statuschecks.RepositoryStatusCheckProvider;
import io.dangernoodle.grt.Workflow;
import io.dangernoodle.grt.internal.GithubWorkflow;
import io.dangernoodle.grt.utils.JsonTransformer;
import io.dangernoodle.grt.utils.RepositoryBuilder;
import io.dangernoodle.grt.utils.RepositoryMerger;


public class UserRepositoryIT
{
    // @RegisterExtension - breaking change as of 5.6.0
    protected static final GitHub github = createGitHub();

    private static final JsonTransformer transformer = new JsonTransformer();

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
        context = new Workflow.Context();
        builder = new RepositoryBuilder(transformer);

        workflow = new GithubWorkflow(GithubClient.createClient(github), new RepositoryStatusCheckProvider());

        // meh - the github api uses commons-lang, so...
        builder.setName("github-repository-tools-test-" + RandomStringUtils.randomAlphanumeric(5).toLowerCase())
               .setOrganization(getOrganization())
               .setInitialize(true);
    }

    @Test
    public void testBranchProtectionStatusChecksOnly() throws Exception
    {
        givenARepositoryWithStatusChecksOnly();
        whenExecuteWorkflow();
        thenRepositoryWasCreated();
        thenStatusChecksOnlyWereConfigured();
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

    private void givenARepositoryWithLabels()
    {
        builder.addLabel("skip-build", Color.from("#006b75"));
    }

    private void givenARepositoryWithStatusChecksOnly()
    {
        builder.requireBranchUpToDate("master", true)
               .addRequiredContext("master", "context");
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

        assertThat(ghRepo.getDescription(), equalTo(repository.getDescription()));
        assertThat(ghRepo.getHomepage(), equalTo(repository.getHomepage()));
    }

    private void thenStatusChecksOnlyWereConfigured() throws IOException
    {
        GHBranchProtection protection = ghRepo.getBranch("master").getProtection();

        assertThat(protection.getRestrictions(), nullValue());
        assertThat(protection.getRequiredReviews(), nullValue());
        assertThat(protection.getEnforceAdmins().isEnabled(), equalTo(false));

        assertThat(protection.getRequiredStatusChecks(), notNullValue());
        assertThat(protection.getRequiredStatusChecks().isRequiresBranchUpToDate(), equalTo(true));
        assertThat(protection.getRequiredStatusChecks().getContexts(), hasItem("context"));
    }

    private void whenExecuteWorkflow() throws Exception
    {
        // the steps will always be invoked w/ a merged repo - duplicated here to prevent NPEs
        RepositoryMerger merger = new RepositoryMerger(transformer);
        repository = merger.merge(builder.build());

        workflow.execute(repository, context);
        ghRepo = context.get(GHRepository.class);
    }

    private static GitHub createGitHub()
    {
        try
        {
            GitHub github = GitHubBuilder.fromEnvironment().build();
            github.checkApiUrlValidity();

            return github;
        }
        catch (IOException e)
        {
            throw new UncheckedIOException(e);
        }
    }
}
