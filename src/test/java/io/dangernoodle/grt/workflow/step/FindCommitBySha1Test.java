package io.dangernoodle.grt.workflow.step;

import static io.dangernoodle.grt.Constants.SHA1;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.kohsuke.github.GHCommit;
import org.mockito.Mock;


public class FindCommitBySha1Test extends AbstractGithubStepTest
{
    @Mock
    private GHCommit mockCommit;

    @Test
    public void testCommitForSha1NotFound() throws Exception
    {
        givenContextContainsKey();
        givenCommitNotFound();
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
        givenCommitIsFound();
        whenExecuteStep();
        thenStatusIsContinue();
        thenCommitAddedToContext();
    }

    @Override
    protected AbstractGithubStep createStep()
    {
        return new FindCommitBy.Sha1(mockClient);
    }

    private void givenCommitIsFound() throws Exception
    {
        when(mockGHRepository.getCommit(COMMIT)).thenReturn(mockCommit);
    }

    private void givenCommitNotFound() throws IOException
    {
        doThrow(IOException.class).when(mockGHRepository).getCommit(COMMIT);
    }

    private void givenContextContainsCommit()
    {
        when(mockContext.contains(GHCommit.class)).thenReturn(true);
    }

    private void givenContextContainsKey()
    {
        when(mockContext.contains(SHA1)).thenReturn(true);
        when(mockContext.get(SHA1)).thenReturn(COMMIT);
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
