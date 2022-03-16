package io.dangernoodle.grt.workflow;

import static io.dangernoodle.grt.Constants.WILDCARD;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.dangernoodle.grt.Workflow;


public class LifecycleWorkflowTest
{
    private String COMMAND = "command";

    private LifecycleWorkflow<Object> lifecycle;

    @Mock
    private Workflow.Context mockContext;

    @Mock
    private Workflow.Lifecycle mockLifecycle;

    @Mock
    private Workflow<Object> mockWorkflow;
    
    private Object object;

    private String OTHER = "other";
    
    @BeforeEach
    public void beforeEach()
    {
        MockitoAnnotations.initMocks(this);
        object = new Object();
    }

    @Test
    public void testCommandsMatch() throws Exception
    {
        givenACommandFilters();
        givenAWorkflow();
        whenExecutePrePost();
        thenPrePostWereExecuted();
    }
    
    @Test
    public void testDelegateExecution() throws Exception
    {
        givenAWorkflow();
        whenExecuteDelegate();
        thenDelegateExecuted();
    }

    @Test
    public void testNoCommandsMatch() throws Exception
    {
        givenNoneMatch();
        givenAWorkflow();
        whenExecutePrePost();
        thenPrePostNotExecuted();
    }

    @Test
    public void testWildcard() throws Exception
    {
        givenAWildcard();
        givenAWorkflow();
        whenExecutePrePost();
        thenPrePostWereExecuted();
    }

    private void givenACommandFilters()
    {
        when(mockLifecycle.getCommands()).thenReturn(List.of(COMMAND, OTHER));
    }

    private void givenAWildcard()
    {
        when(mockLifecycle.getCommands()).thenReturn(List.of(OTHER, WILDCARD));
    }

    private void givenAWorkflow()
    {
        lifecycle = new LifecycleWorkflow<>(COMMAND, mockWorkflow, List.of(mockLifecycle));
    }

    private void givenNoneMatch()
    {
        when(mockLifecycle.getCommands()).thenReturn(List.of("none"));
    }

    private void thenDelegateExecuted() throws Exception
    {
        verify(mockWorkflow).preExecution();
        verify(mockWorkflow).execute(object, mockContext);
        verify(mockWorkflow).postExecution();
    }

    private void thenPrePostNotExecuted() throws Exception
    {
        verify(mockLifecycle, times(0)).preExecution();
        verify(mockLifecycle, times(0)).postExecution();
    }

    private void thenPrePostWereExecuted() throws Exception
    {
        verify(mockLifecycle).preExecution();
        verify(mockLifecycle).postExecution();
    }

    private void whenExecuteDelegate() throws Exception
    {
        lifecycle.execute(object, mockContext);
    }

    private void whenExecutePrePost() throws Exception
    {
        lifecycle.preExecution();
        lifecycle.postExecution();
    }
}
