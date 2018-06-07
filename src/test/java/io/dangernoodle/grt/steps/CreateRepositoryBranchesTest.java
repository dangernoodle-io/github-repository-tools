package io.dangernoodle.grt.steps;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kohsuke.github.GHBranch;
import org.kohsuke.github.GHRepository;
import org.mockito.Mock;

import io.dangernoodle.grt.internal.GithubWorkflow;


public class CreateRepositoryBranchesTest extends AbstractGithubWorkflowStepTest
{
    private Map<String, GHBranch> existingBranches;

    @Mock
    private GHBranch mockGHBranch;

    @Override
    @BeforeEach
    public void beforeEach() throws Exception
    {
        super.beforeEach();

        existingBranches = new HashMap<>();
        existingBranches.put("master", mockGHBranch);

        when(mockContext.get(GHRepository.class)).thenReturn(mockGHRepository);
        when(mockGHRepository.getBranches()).thenReturn(existingBranches);

        when(mockGHRepository.getBranch("master")).thenReturn(mockGHBranch);
        when(mockGHRepository.getDefaultBranch()).thenReturn("master");
    }

    @Test
    public void testChangeDefaultBranch() throws Exception
    {
        givenDefaultBranchIsOther();
        givenOtherBranchExists();
        whenExecuteStep();
        thenDefaultBranchIsChanged();
    }

    @Test
    public void testCreateAndChangeDefaultBranch() throws Exception
    {
        givenDefaultBranchIsOther();
        givenCommitForInitialization();
        whenExecuteStep();
        thenOtherBranchIsCreated();
        thenDefaultBranchIsChanged();
    }

    @Test
    public void testCreateBranchesNoneExist() throws Exception
    {
        givenBranchesToCreate();
        givenCommitForInitialization();
        givenNoExistingBranches();
        whenExecuteStep();
        thenAllBranchesCreated();
    }

    @Test
    public void testCreateBranchesOtherExists() throws Exception
    {
        givenBranchesToCreate();
        givenCommitForInitialization();
        givenOtherBranchExists();
        whenExecuteStep();
        thenV2BranchesCreated();
    }

    @Test
    public void testNoExistingCommit() throws Exception
    {
        whenExecuteStep();
        thenNoBranchesCreatedOrChanged();
    }

    @Override
    protected GithubWorkflow.Step createStep()
    {
        return new CreateRepositoryBranches(mockClient);
    }

    private void givenBranchesToCreate()
    {
        repoBuilder.addOtherBranch("other")
                   .addOtherBranch("v2");
    }

    private void givenCommitForInitialization()
    {
        when(mockGHBranch.getSHA1()).thenReturn("abc123");
    }

    private void givenDefaultBranchIsOther()
    {
        repoBuilder.setPrimaryBranch("other");
    }

    private void givenNoExistingBranches() throws IOException
    {
        when(mockGHRepository.getBranches()).thenReturn(Collections.emptyMap());
    }

    private void givenOtherBranchExists()
    {
        existingBranches.put("other", mockGHBranch);
    }

    private void thenAllBranchesCreated() throws IOException
    {
        verify(mockGHRepository).createRef("refs/heads/other", "abc123");
        verify(mockGHRepository).createRef("refs/heads/v2", "abc123");
    }

    private void thenDefaultBranchIsChanged() throws IOException
    {
        verify(mockGHRepository).setDefaultBranch(repository.getSettings().getBranches().getDefault());
    }

    private void thenNoBranchesCreatedOrChanged() throws IOException
    {
        verify(mockGHRepository, times(0)).createRef(any(), any());
        verify(mockGHRepository, times(0)).setDefaultBranch(any());
    }

    private void thenOtherBranchIsCreated() throws IOException
    {
        verify(mockGHRepository).createRef("refs/heads/other", "abc123");
    }

    private void thenV2BranchesCreated() throws IOException
    {
        verify(mockGHRepository).createRef("refs/heads/v2", "abc123");
        verify(mockGHRepository, times(0)).createRef("refs/heads/other", "abc123");
    }
}
