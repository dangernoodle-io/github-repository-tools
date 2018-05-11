package io.dangernoodle.grt.steps;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kohsuke.github.GHBranch;
import org.kohsuke.github.GHBranchProtectionBuilder;
import org.kohsuke.github.GHRepository;
import org.mockito.Mock;

import io.dangernoodle.grt.internal.GithubWorkflow;


public class EnableBranchProtectionsTest extends AbstractGithubWorkflowStepTest
{
    @Mock
    private GHBranch mockGHBranch;

    @Mock
    private GHBranchProtectionBuilder mockGHBuilder;

    @Override
    @BeforeEach
    public void beforeEach() throws Exception
    {
        super.beforeEach();

        when(mockGHBranch.enableProtection()).thenReturn(mockGHBuilder);
        when(mockGHRepository.getBranch("master")).thenReturn(mockGHBranch);
        when(mockContext.get(GHRepository.class)).thenReturn(mockGHRepository);
    }

    @Test
    public void testBranchDoesNotExist() throws Exception
    {
        givenABranchProtection();
        givenABranchThatDoesntExist();
        whenExecuteStep();
        thenBranchIsSkipped();
    }

    @Test
    public void testDisableProtection() throws Exception
    {
        givenAProtectedBranch();
        whenExecuteStep();
        thenProtectionIsDisabled();
    }

    @Test
    public void testEnableProtectionOnly() throws Exception
    {
        givenABranchProtection();
        whenExecuteStep();
        thenBranchProtectionIsEnabled();
        thenNoOtherProtectionsEnabled();
    }

    @Override
    protected GithubWorkflow.Step createStep()
    {
        return new EnableBranchProtections(mockClient);
    }

    private void givenABranchProtection()
    {
        repoBuilder.enableBranchProtection("master");
    }

    private void givenABranchThatDoesntExist() throws IOException
    {
        when(mockGHRepository.getBranch("master")).thenReturn(null);
    }

    private void givenAProtectedBranch()
    {
        when(mockGHBranch.isProtected()).thenReturn(true);
    }

    private void thenBranchIsSkipped()
    {
        verifyNoMoreInteractions(mockGHBranch);
    }

    private void thenBranchProtectionIsEnabled() throws IOException
    {
        verify(mockGHBranch).enableProtection();
        verify(mockGHBuilder).enable();
    }

    private void thenNoOtherProtectionsEnabled()
    {
        verifyNoMoreInteractions(mockGHBuilder);
    }

    private void thenProtectionIsDisabled() throws IOException
    {
        verify(mockGHBranch).disableProtection();
    }
}
