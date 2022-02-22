package io.dangernoodle.grt.steps;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kohsuke.github.GHHook;
import org.mockito.Mock;


public class ClearWebhooksTest extends AbstractGithubWorkflowStepTest
{
    @Mock
    private GHHook mockHook;

    @Override
    @BeforeEach
    public void beforeEach() throws Exception
    {
        super.beforeEach();

    }

    @Test
    public void testClearAll() throws Exception
    {
        givenRepoHasHooks();
        givenClearAllHooks();
        whenExecuteStep();
        thenHooksAreCleared();
    }

    @Test
    public void testClearAllNoOp() throws Exception
    {
        givenRepoHasNoHooks();
        givenClearAllHooks();
        whenExecuteStep();
        thenNoHooksCleared();
    }

    @Test
    public void testClearDisabled() throws Exception
    {
        givenClearAllDisabled();
        whenExecuteStep();
        thenNoInteractions();
    }

    @Override
    protected AbstractGithubStep createStep()
    {
        return new ClearWebhooks(mockClient);
    }

    private void givenClearAllDisabled()
    {
        when(mockContext.getArg(ClearWebhooks.CLEAR_WEBHOOKS, false)).thenReturn(false);
    }

    private void givenClearAllHooks()
    {
        when(mockContext.getArg(ClearWebhooks.CLEAR_WEBHOOKS, false)).thenReturn(true);
    }

    private void givenRepoHasHooks() throws IOException
    {
        when(mockGHRepository.getHooks()).thenReturn(Arrays.asList(mockHook));
    }

    private void givenRepoHasNoHooks() throws IOException
    {
        when(mockGHRepository.getHooks()).thenReturn(Collections.emptyList());
    }

    private void thenHooksAreCleared() throws IOException
    {
        verify(mockHook).delete();
    }

    private void thenNoHooksCleared() throws IOException
    {
        verify(mockHook, times(0)).delete();
    }

    private void thenNoInteractions()
    {
        verifyNoInteractions(mockGHRepository, mockHook);
    }
}
