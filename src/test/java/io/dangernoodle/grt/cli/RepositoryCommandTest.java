package io.dangernoodle.grt.cli;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.dangernoodle.grt.Arguments;
import io.dangernoodle.grt.Repository;
import io.dangernoodle.grt.internal.RepositoryMerger;
import io.dangernoodle.grt.internal.WorkflowExecutor;


public class RepositoryCommandTest
{
    private Exception exception;

    private RepositoryCommand.Executor executor;

    @Mock
    private Arguments mockArguments;

    @Mock
    private File mockFile1;

    @Mock
    private File mockFile2;

    @Mock
    private RepositoryMerger mockMerger;

    @Mock
    private Repository mockRepository;

    @Mock
    private WorkflowExecutor mockWorkflowExecutor;

    @BeforeEach
    public void beforeEach()
    {
        MockitoAnnotations.initMocks(this);

        when(mockMerger.merge()).thenReturn(mockRepository);
        executor = new RepositoryCommand.Executor(mockArguments, mockWorkflowExecutor)
        {
            @Override
            RepositoryMerger createRepositoryMerger(File defaults, File overrides) throws IOException
            {
                return mockMerger;
            }
        };
    }

    @Test
    public void testExecutorSuceeds() throws Exception
    {
        whenExecuteCommand();
        thenRepositoriesMerged();
        thenWorkflowExecuted();
    }

    @Test
    public void testWorkflowFails() throws Exception
    {
        givenAWorkflowFailure();
        whenExecuteCommand();
        thenRepositoriesMerged();
        thenWorkflowThrewException();
    }

    private void givenAWorkflowFailure() throws Exception
    {
        doThrow(Exception.class).when(mockWorkflowExecutor).execute(mockRepository);
    }

    private void thenRepositoriesMerged()
    {
        verify(mockMerger).merge();
    }

    private void thenWorkflowExecuted() throws Exception
    {
        verify(mockWorkflowExecutor).execute(mockRepository);
    }

    private void thenWorkflowThrewException()
    {
        assertThat(exception, notNullValue());
    }

    private void whenExecuteCommand()
    {
        try
        {
            executor.execute(mockFile1, mockFile2);
        }
        catch (Exception e)
        {
            exception = e;
        }
    }
}