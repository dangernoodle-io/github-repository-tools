package io.dangernoodle.grt;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


public class WorkflowBasicTest
{
    private Workflow workflow;
    
    @Mock
    private Workflow.Context mockContext;

    @Mock
    private Repository mockRepository;
    
    @Mock
    private Workflow.Step mockStep;
    
    @BeforeEach
    public void beforeEach()
    {
        MockitoAnnotations.initMocks(this);

        workflow = new Workflow.Basic()
        {
            @Override
            public String getName()
            {
                return "basic";
            }

            @Override
            protected Collection<Step> createSteps()
            {
                return Arrays.asList(mockStep, mockStep, mockStep);
            }
        };
    }
    
    @Test
    public void testWorkflowAborts() throws Exception
    {
        givenAWorkflowThatAborts();
        whenExecuteWorkflow();
        thenWorkflowAborts();
    }

    @Test
    public void testWorkflowFinishes() throws Exception
    {
        givenAWorkflowThatFinishes();
        whenExecuteWorkflow();
        thenWorkflowCompletes();
    }

    private void givenAWorkflowThatAborts() throws Exception
    {
        when(mockStep.execute(mockRepository, mockContext)).thenReturn(Workflow.Status.CONTINUE)
                                                           .thenReturn(Workflow.Status.SKIP);
    }

    private void givenAWorkflowThatFinishes() throws Exception
    {
        when(mockStep.execute(mockRepository, mockContext)).thenReturn(Workflow.Status.CONTINUE)
                                                           .thenReturn(Workflow.Status.CONTINUE)
                                                           .thenReturn(Workflow.Status.CONTINUE);
    }

    private void thenWorkflowAborts() throws Exception
    {
        verify(mockStep, times(2)).execute(mockRepository, mockContext);
    }

    private void thenWorkflowCompletes() throws Exception
    {
        verify(mockStep, times(3)).execute(mockRepository, mockContext);
    }

    private void whenExecuteWorkflow() throws Exception
    {
        workflow.execute(mockRepository, mockContext);
    }
}
