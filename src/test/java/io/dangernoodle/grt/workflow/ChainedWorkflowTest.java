package io.dangernoodle.grt.workflow;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.dangernoodle.grt.Workflow;


public class ChainedWorkflowTest
{
    private ChainedWorkflow<Object> chained;

    private boolean ignoreErrors;

    @Mock
    private Workflow.Context mockContext;

    @Mock
    private Workflow<Object> mockWorkflow;

    private Object object;

    @BeforeEach
    public void beforeEach()
    {
        MockitoAnnotations.initMocks(this);
        object = new Object();
    }

    @Test
    public void testSuccessfulExecution() throws Exception
    {
        givenAWorkflow();
        whenExecuteWorkflow();
        thenWorkflowIsExecuted();
    }
    
    @Test
    public void testWorflowException() throws Exception
    {
        givenAWorkflow();
        givenAWorkflowException();
        whenExecuteWorkflowException();
        thenWorkflowWasAborted();
    }

    @Test
    public void testWorflowIgnoreErrors() throws Exception
    {
        givenIgnoreErrors();
        givenAWorkflow();
        givenAWorkflowException();
        whenExecuteWorkflow();
        thenWorkflowIsExecuted();
    }
    
    private void givenAWorkflow()
    {
        chained = new ChainedWorkflow<>(ignoreErrors, mockWorkflow);
    }
    
    private void givenAWorkflowException() throws Exception
    {
        doThrow(Exception.class).when(mockWorkflow).execute(object, mockContext);
    }
    
    private void givenIgnoreErrors()
    {
        ignoreErrors = true;
    }
    
    private void thenWorkflowIsExecuted() throws Exception
    {
        InOrder ordered = inOrder(mockWorkflow);

        ordered.verify(mockWorkflow).preExecution();
        ordered.verify(mockWorkflow).execute(object, mockContext);
        ordered.verify(mockWorkflow).postExecution();
    }

    private void thenWorkflowWasAborted() throws Exception
    {
        verify(mockWorkflow, times(1)).execute(object, mockContext);
    }

    private void whenExecuteWorkflow() throws Exception
    {
        chained.execute(object, mockContext);
    }

    private void whenExecuteWorkflowException()
    {
        assertThrows(Exception.class, this::whenExecuteWorkflow);
    }
}
