package io.dangernoodle.grt.workflows;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.dangernoodle.grt.Repository;
import io.dangernoodle.grt.Workflow;


public class StepWorkflowTest
{
    @Mock
    protected Workflow.Context mockContext;

    @Mock
    protected Repository mockRepository;

    @Mock
    private Workflow.Step mockStep;

    @BeforeEach
    public void beforeEach()
    {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testWorkflowCompletes() throws Exception
    {
        givenStepsContinue();
        whenExecuteWorkflow();
        thenStepsCompleted();
    }

    @Test
    public void testWorkflowSkips() throws Exception
    {
        givenStepsSkip();
        whenExecuteWorkflow();
        thenStepsSkipped();
    }

    private void whenExecuteWorkflow() throws Exception
    {
        new StepWorkflow("workflow", List.of(mockStep, mockStep, mockStep)).execute(mockRepository, mockContext);
    }

    private void givenStepsContinue() throws Exception
    {
        when(mockStep.execute(mockRepository, mockContext)).thenReturn(Workflow.Status.CONTINUE);
    }

    private void givenStepsSkip() throws Exception
    {
        when(mockStep.execute(mockRepository, mockContext)).thenReturn(Workflow.Status.CONTINUE, Workflow.Status.SKIP);
    }

    private void thenStepsCompleted() throws Exception
    {
        verify(mockStep, times(3)).execute(mockRepository, mockContext);
    }

    private void thenStepsSkipped() throws Exception
    {
        verify(mockStep, times(2)).execute(mockRepository, mockContext);
    }
}
