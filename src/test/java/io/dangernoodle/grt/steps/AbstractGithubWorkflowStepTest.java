package io.dangernoodle.grt.steps;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.kohsuke.github.GHRepository;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.dangernoodle.grt.GithubClient;
import io.dangernoodle.grt.Repository;
import io.dangernoodle.grt.Workflow;
import io.dangernoodle.grt.internal.GithubWorkflow;
import io.dangernoodle.grt.internal.RepositoryBuilder;
import io.dangernoodle.grt.internal.RepositoryMerger;


public abstract class AbstractGithubWorkflowStepTest
{
    private static final Repository defaults = new RepositoryBuilder().build();

    @Mock
    protected GithubClient mockClient;

    @Mock
    protected Workflow.Context mockContext;

    @Mock
    protected GHRepository mockGHRepository;

    protected RepositoryBuilder repoBuilder;

    protected Repository repository;

    private GithubWorkflow.Step step;

    @BeforeEach
    @SuppressWarnings("unused")
    public void beforeEach() throws Exception
    {
        MockitoAnnotations.initMocks(this);

        repoBuilder = new RepositoryBuilder();
        repoBuilder.setName("repository")
                   .setOrganization("org");

        step = createStep();
    }

    protected abstract GithubWorkflow.Step createStep();

    protected final void whenExecuteStep() throws IOException
    {
        // the steps will always be invoked w/ a merged repo - duplicated here to prevent NPEs
        repository = new RepositoryMerger(defaults, repoBuilder.build()).merge();
        step.execute(repository, mockContext);
    }
}
