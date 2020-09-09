package io.dangernoodle.grt.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.dangernoodle.grt.GithubClient;
import io.dangernoodle.grt.Repository;
import io.dangernoodle.grt.Workflow;
import io.dangernoodle.grt.ext.statuschecks.StatusCheckProvider;


public class GithubWorkflowTest
{
    private static final int STEP_COUNT = 6;
    
    private Collection<GithubWorkflow.Step> githubSteps;

    private StatusCheckProvider mockCheckProvider;

    @Mock
    private GithubClient mockClient;

    private Workflow.Context mockContext;

    @Mock
    private Repository mockRepository;

    @Mock
    private GithubWorkflow.Step mockStep;

    @Mock
    private GithubWorkflow workflow;

    @BeforeEach
    public void beforeEach()
    {
        MockitoAnnotations.initMocks(this);

        workflow = new GithubWorkflow(mockClient, mockCheckProvider)
        {
            @Override
            Collection<GithubWorkflow.Step> createSteps()
            {
                githubSteps = super.createSteps();
                return Arrays.asList(mockStep, mockStep, mockStep);
            }
        };
    }

    @Test
    public void testVerifyStepCount() throws Exception
    {
        whenExecuteWorkflow();
        thenStepCountIsCorrect();
        
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

    private void thenStepCountIsCorrect()
    {
        assertEquals(STEP_COUNT, githubSteps.size());
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
