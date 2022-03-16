package io.dangernoodle.grt.workflow.step;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.kohsuke.github.GHRepository;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.dangernoodle.grt.Repository;
import io.dangernoodle.grt.Workflow;
import io.dangernoodle.grt.Workflow.Status;
import io.dangernoodle.grt.client.GithubClient;
import io.dangernoodle.grt.repository.RepositoryBuilder;
import io.dangernoodle.grt.repository.RepositoryMerger;
import io.dangernoodle.grt.util.JsonTransformer;


public abstract class AbstractGithubStepTest
{
    protected static final String COMMIT = "3ac68f50226751ae44654497a38e220437ee677";

    protected static final JsonTransformer transformer = new JsonTransformer();

    @Mock
    protected GithubClient mockClient;

    @Mock
    protected Workflow.Context mockContext;

    @Mock
    protected GHRepository mockGHRepository;

    protected RepositoryBuilder repoBuilder;

    protected Repository repository;

    protected Status status;

    @BeforeEach
    public void beforeEach() throws Exception
    {
        MockitoAnnotations.initMocks(this);

        repoBuilder = createBuilder();
        repoBuilder.setName("repository")
                   .setOrganization("org");

        when(mockContext.getGHRepository()).thenReturn(mockGHRepository);
    }

    protected abstract AbstractGithubStep createStep();

    protected final void thenStatusIsContinue()
    {
        assertEquals(status, Status.CONTINUE);
    }

    protected final void thenStatusIsSkip()
    {
        assertEquals(status, Status.SKIP);
        verifyNoMoreInteractions(mockClient);
    }

    protected final void whenExecuteStep() throws Exception
    {
        // the steps will always be invoked w/ a merged repo - duplicated here to prevent NPEs
        RepositoryMerger merger = new RepositoryMerger(transformer);
        repository = merger.merge(repoBuilder.build());

        status = createStep().execute(repository, mockContext);
    }

    private RepositoryBuilder createBuilder()
    {
        return new RepositoryBuilder(transformer);
    }
}
