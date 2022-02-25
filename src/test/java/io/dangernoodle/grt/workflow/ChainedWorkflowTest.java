package io.dangernoodle.grt.workflow;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.dangernoodle.grt.Repository;
import io.dangernoodle.grt.Workflow;
import io.dangernoodle.grt.workflow.ChainedWorkflow;


public class ChainedWorkflowTest
{
    private boolean ignoreErrors;

    @Mock
    private Workflow.Context mockContext;

    @Mock
    private Repository mockRepository;

    @Mock
    private Workflow mockWorkflow;

    private ChainedWorkflow workflow;

    private List<Workflow> workflows;

    @BeforeEach
    public void beforeEach()
    {
        MockitoAnnotations.initMocks(this);
        workflows = Arrays.asList(mockWorkflow, mockWorkflow, mockWorkflow);
    }

    @Test
    public void testPrePostExecution()
    {
        givenAChainedWorkflow();
        whenExecutePrePost();
        thenPrePostExecuted();
    }

    @Test
    public void testSuccessfulExecution() throws Exception
    {
        givenAChainedWorkflow();
        whenExecuteWorkflow();
        thenWorkflowIsExecuted();
    }

    @Test
    public void testWorflowException() throws Exception
    {
        givenAChainedWorkflow();
        givenAWorkflowException();
        whenExecuteWorkflowException();
        thenWorkflowWasAborted();
    }
    
    @Test
    public void testWorflowIgnoreErrors() throws Exception
    {
        givenIgnoreErrors();
        givenAChainedWorkflow();
        givenAWorkflowException();
        whenExecuteWorkflow();
        thenWorkflowIsExecuted();
    }

    private void givenAChainedWorkflow()
    {
        workflow = new ChainedWorkflow(workflows, ignoreErrors);
    }

    private void givenAWorkflowException() throws Exception
    {
        doNothing().doThrow(Exception.class).when(mockWorkflow).execute(mockRepository, mockContext);
       // doThrow(Exception.class).when(mockWorkflow).execute(mockRepository, mockContext);        
    }

    private void givenIgnoreErrors()
    {
       ignoreErrors = true;
    }

    private void thenPrePostExecuted()
    {
        verify(mockWorkflow, times(3)).preExecution();
        verify(mockWorkflow, times(3)).postExecution();
    }

    private void thenWorkflowIsExecuted() throws Exception
    {
        verify(mockWorkflow, times(3)).execute(mockRepository, mockContext);
    }

    private void thenWorkflowWasAborted() throws Exception
    {
        verify(mockWorkflow, times(2)).execute(mockRepository, mockContext);
    }

    private void whenExecutePrePost()
    {
        workflow.preExecution();
        workflow.postExecution();
    }

    private void whenExecuteWorkflow() throws Exception
    {
        workflow.execute(mockRepository, mockContext);
    }

    private void whenExecuteWorkflowException()
    {
        assertThrows(Exception.class, this::whenExecuteWorkflow);
    }
}
