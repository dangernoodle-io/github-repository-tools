package io.dangernoodle.grt.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class GithubWorkflowTest
{
    private static final int STEP_COUNT = 7;

    private GithubWorkflow workflow;

    @BeforeEach
    public void beforeEach()
    {
        workflow = new GithubWorkflow(null, null);
    }

    @Test
    public void testVerifyStepCount() throws Exception
    {
        assertEquals(STEP_COUNT, workflow.createSteps().size());
    }
}
