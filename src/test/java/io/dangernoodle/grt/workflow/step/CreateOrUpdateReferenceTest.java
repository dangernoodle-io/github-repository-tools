package io.dangernoodle.grt.workflow.step;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHRef;
import org.kohsuke.github.GHRepository;
import org.mockito.Mock;

import io.dangernoodle.grt.Constants;


public class CreateOrUpdateReferenceTest extends AbstractGithubStepTest
{
    private static final String REF_NAME = "ref/name";

    @Mock
    private GHCommit mockCommit;

    @Mock
    private GHRef mockRef;

    @Override
    @BeforeEach
    public void beforeEach() throws Exception
    {
        super.beforeEach();

        when(mockCommit.getSHA1()).thenReturn(COMMIT);
        when(mockContext.get(Constants.REF)).thenReturn(REF_NAME);
        when(mockContext.get(GHCommit.class)).thenReturn(mockCommit);
    }

    @Test
    public void testCreateReference() throws Exception
    {
        givenAMissingRef();
        whenExecuteStep();
        thenReferenceIsCreated();
        thenStatusIsContinue();
    }

    @Test
    public void testUpdateReference() throws Exception
    {
        givenARefExists();
        whenExecuteStep();
        thenReferenceIsUpdated();
        thenStatusIsContinue();
    }

    @Test

    @Override
    protected AbstractGithubStep createStep()
    {
        return new CreateOrUpdateReference(mockClient)
        {
            @Override
            Stream<GHRef> getRefStream(GHRepository ghRepo) throws IOException
            {
                return Stream.of(mockRef);
            }
        };
    }

    private void givenAMissingRef()
    {
        when(mockRef.getRef()).thenReturn("does/not/match");
    }

    private void givenARefExists()
    {
        when(mockRef.getRef()).thenReturn(REF_NAME);
    }

    private void thenReferenceIsCreated() throws IOException
    {
        verify(mockGHRepository).createRef(REF_NAME, COMMIT);
    }

    private void thenReferenceIsUpdated() throws IOException
    {
        verify(mockRef).updateTo(COMMIT, true);
    }
}
