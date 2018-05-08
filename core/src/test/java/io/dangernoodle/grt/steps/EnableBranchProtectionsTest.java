package io.dangernoodle.grt.steps;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.kohsuke.github.GHRepository;

import io.dangernoodle.grt.internal.GithubWorkflow;


@Disabled
public class EnableBranchProtectionsTest extends AbstractGithubWorkflowStepTest
{
    @Override
    @BeforeEach
    public void beforeEach() throws Exception
    {
        super.beforeEach();
        when(mockContext.get(GHRepository.class)).thenReturn(mockGHRepository);
    }

    @Override
    protected GithubWorkflow.Step createStep()
    {
        return new EnableBranchProtections(mockClient);
    }
}
