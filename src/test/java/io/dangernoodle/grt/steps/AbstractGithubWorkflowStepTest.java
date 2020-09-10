package io.dangernoodle.grt.steps;

import org.junit.jupiter.api.BeforeEach;
import org.kohsuke.github.GHRepository;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.dangernoodle.grt.GithubClient;
import io.dangernoodle.grt.Repository;
import io.dangernoodle.grt.Workflow;
import io.dangernoodle.grt.internal.GithubWorkflow;
import io.dangernoodle.grt.utils.JsonTransformer;
import io.dangernoodle.grt.utils.RepositoryBuilder;
import io.dangernoodle.grt.utils.RepositoryMerger;


public abstract class AbstractGithubWorkflowStepTest
{
    protected static final JsonTransformer transformer = new JsonTransformer();

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

        repoBuilder = createBuilder();
        repoBuilder.setName("repository")
                   .setOrganization("org");

        step = createStep();
    }

    protected abstract GithubWorkflow.Step createStep();

    protected final void whenExecuteStep() throws Exception
    {
        // the steps will always be invoked w/ a merged repo - duplicated here to prevent NPEs
        RepositoryMerger merger = new RepositoryMerger(transformer);
        repository = merger.merge(repoBuilder.build());

        step.execute(repository, mockContext);
    }

    private RepositoryBuilder createBuilder()
    {
        return new RepositoryBuilder(transformer);
    }
}
