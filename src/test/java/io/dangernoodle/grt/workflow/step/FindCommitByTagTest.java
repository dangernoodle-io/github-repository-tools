package io.dangernoodle.grt.workflow.step;

import static io.dangernoodle.grt.Constants.TAG;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHTag;
import org.mockito.Mock;


public class FindCommitByTagTest extends AbstractGithubStepTest
{
    private static final String REPO_TAG = "repo-1.0.0";

    @Mock
    private GHCommit mockCommit;

    @Mock
    private GHTag mockTag;

    @Test
    public void testCommitForTagNotFound()
    {
        givenContextContainsKey();
        thenFindCommitThrowsException();
    }

    @Test
    public void testFindByAlreadyExists() throws Exception
    {
        givenContextContainsCommit();
        whenExecuteStep();
        thenFindByNotPerformed();
        thenStatusIsContinue();
    }

    @Test
    public void testFindByNotFound() throws Exception
    {
        whenExecuteStep();
        thenFindByNotPerformed();
        thenStatusIsContinue();
    }

    @Test
    public void testFindBySuccess() throws Exception
    {
        givenContextContainsKey();
        givenTagHasCommit();
        whenExecuteStep();
        thenStatusIsContinue();
        thenCommitAddedToContext();
    }

    @Override
    protected AbstractGithubStep createStep()
    {
        return new FindCommitBy.Tag(mockClient)
        {
            @Override
            Stream<GHTag> getTagStream(GHRepository ghRepo) throws IOException
            {
                return Stream.of(mockTag);
            }
        };
    }

    private void givenContextContainsCommit()
    {
        when(mockContext.contains(GHCommit.class)).thenReturn(true);
    }

    private void givenContextContainsKey()
    {
        when(mockContext.contains(TAG)).thenReturn(true);
        when(mockContext.get(TAG)).thenReturn(REPO_TAG);
    }

    private void givenTagHasCommit()
    {
        when(mockTag.getName()).thenReturn(REPO_TAG);
        when(mockTag.getCommit()).thenReturn(mockCommit);
    }

    private void thenCommitAddedToContext()
    {
        verify(mockContext).add(mockCommit);
    }

    private void thenFindByNotPerformed()
    {
        verifyNoInteractions(mockGHRepository);
        verify(mockContext, times(0)).add(any());
    }

    private void thenFindCommitThrowsException()
    {
        assertThrows(IOException.class, this::whenExecuteStep);
    }
}
