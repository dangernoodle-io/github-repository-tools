package io.dangernoodle.grt.workflow;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.dangernoodle.grt.Repository;
import io.dangernoodle.grt.Workflow;


public class CommandWorkflowTest
{
    private static final String COMMAND = "command";
    
    private static final String OTHER = "other";

    private boolean ignoreErrors;

    @Mock
    private Workflow.Context mockContext;

    @Mock
    private Repository mockRepository;

    @Mock
    private Workflow<Repository> mockWorkflow;

    @BeforeEach
    public void beforeEach()
    {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCommandWorkflowFound() throws Exception
    {
        givenAWorkflowCommand();
        givenACommandDefinedInWorkflow();
        whenExecuteWorkflow();
        thenWorkflowWasInvoked();
    }

    @Test
    public void testAutoAddCommand() throws Exception
    {
        givenAWorkflowCommand();
        givenAutoAddEnabled();
        whenExecuteWorkflow();
        thenWorkflowWasInvoked();
    }

    @Test
    public void testNoWorkflowsDefined() throws Exception
    {
        assertThrows(IllegalStateException.class, this::whenExecuteWorkflow);
    }
    
    @Test
    public void testCommandWorkflowNotFound()
    {
        givenOtherCommandDefinedInWorkflow();
        assertThrows(IllegalStateException.class, this::whenExecuteWorkflow);
    }
    
    private void givenOtherCommandDefinedInWorkflow()
    {
        when(mockRepository.getWorkflows(COMMAND)).thenReturn(List.of(OTHER));
    }

    private void givenACommandDefinedInWorkflow()
    {
        when(mockRepository.getWorkflows(COMMAND)).thenReturn(List.of(COMMAND));
    }

    private void givenAutoAddEnabled()
    {
        when(mockContext.isAutoAddWorkflowEnabled()).thenReturn(true);
    }

    private void givenAWorkflowCommand()
    {
        when(mockWorkflow.getName()).thenReturn(COMMAND);
    }

    private void thenWorkflowWasInvoked() throws Exception
    {
        verify(mockWorkflow).preExecution();
        verify(mockWorkflow).execute(mockRepository, mockContext);
        verify(mockWorkflow).postExecution();
    }

    private void whenExecuteWorkflow() throws Exception
    {
        new CommandWorkflow(COMMAND, ignoreErrors, List.of(mockWorkflow)).execute(mockRepository, mockContext);
    }
}
