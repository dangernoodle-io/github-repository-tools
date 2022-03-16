package io.dangernoodle.grt.workflow;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.dangernoodle.grt.Workflow;


public class CompositeWorkflowTest
{
    private CompositeWorkflow<Object> composite;

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
    public void testPrePostExecution() throws Exception
    {
        givenAWorkflow();
        whenExecutePrePost();
        thenPrePostExecuted();
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
        composite = new CompositeWorkflow<>(ignoreErrors, List.of(mockWorkflow, mockWorkflow, mockWorkflow));
    }

    private void givenAWorkflowException() throws Exception
    {
        doNothing().doThrow(Exception.class).when(mockWorkflow).execute(object, mockContext);
    }

    private void givenIgnoreErrors()
    {
        ignoreErrors = true;
    }

    private void thenPrePostExecuted() throws Exception
    {
        verify(mockWorkflow, times(3)).preExecution();
        verify(mockWorkflow, times(3)).postExecution();
    }

    private void thenWorkflowIsExecuted() throws Exception
    {
        verify(mockWorkflow, times(3)).execute(object, mockContext);
    }

    private void thenWorkflowWasAborted() throws Exception
    {
        verify(mockWorkflow, times(2)).execute(object, mockContext);
    }

    private void whenExecutePrePost() throws Exception
    {
        composite.preExecution();
        composite.postExecution();
    }

    private void whenExecuteWorkflow() throws Exception
    {
        composite.execute(object, mockContext);
    }

    private void whenExecuteWorkflowException()
    {
        assertThrows(Exception.class, this::whenExecuteWorkflow);
    }
}
