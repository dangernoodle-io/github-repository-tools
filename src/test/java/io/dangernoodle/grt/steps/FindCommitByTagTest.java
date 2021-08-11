package io.dangernoodle.grt.steps;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Spliterator;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHTag;
import org.mockito.Mock;

import io.dangernoodle.grt.internal.GithubWorkflow.Step;


public class FindCommitByTagTest extends AbstractGithubWorkflowStepTest
{
    private static final String TAG = "repo-1.0.0";

    @Mock
    private GHCommit mockCommit;

    @Mock
    private Spliterator<GHTag> mockSpliterator;

    @Mock
    private GHTag mockTag;

    @Override
    @BeforeEach
    public void beforeEach() throws Exception
    {
        super.beforeEach();

        when(mockCommit.getSHA1()).thenReturn(COMMIT);
        when(mockTag.getCommit()).thenReturn(mockCommit);
        when(mockContext.getArg("commitTag")).thenReturn("repo-1.0.0");
    }

    @Test
    public void testTagDoesNotExist()
    {
        assertThrows(IllegalStateException.class, () -> {
            whenExecuteStep();
        });
    }

    @Test
    public void testTagExists() throws Exception
    {
        givenATagExists();
        whenExecuteStep();
        thenCommitIsFound();
        thenStatusIsContinue();
    }

    @Override
    protected Step createStep()
    {
        return new FindCommitByTag(mockClient)
        {
            @Override
            Stream<GHTag> getTagStream(GHRepository ghRepo) throws IOException
            {
                return Stream.of(mockTag);
            }
        };
    }

    private void givenATagExists()
    {
        when(mockTag.getName()).thenReturn(TAG);
    }

    private void thenCommitIsFound()
    {
        verify(mockContext).add(mockCommit);
    }
}
