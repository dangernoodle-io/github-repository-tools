package io.dangernoodle.grt.workflows;

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

    private boolean ignoreErrors;

    @Mock
    private Workflow.Context mockContext;

    @Mock
    private Repository mockRepository;

    @Mock
    private Workflow mockWorkflow;

    @BeforeEach
    public void beforeEach()
    {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCommandDefinedInWorkflow() throws Exception
    {
        givenAWorkflowCommand();
        givenACommandDefinedInWorkflow();
        whenExecuteWorkflow();
        thenWorkflowWasInvoked();
    }

    @Test
    public void testCommandNotDefinedInWorkflow() throws Exception
    {
        givenAWorkflowCommand();
        whenExecuteWorkflow();
        thenWorkflowWasInvoked();
    }

    @Test
    public void testWorkflowNotFound() throws Exception
    {
        assertThrows(IllegalStateException.class, this::whenExecuteWorkflow);
    }

    private void givenACommandDefinedInWorkflow()
    {
        when(mockRepository.getWorkflows(COMMAND)).thenReturn(List.of(COMMAND));
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
        new CommandWorkflow(COMMAND, List.of(mockWorkflow), ignoreErrors).execute(mockRepository, mockContext);
    }

}
