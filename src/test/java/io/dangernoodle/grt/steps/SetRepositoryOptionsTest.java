package io.dangernoodle.grt.steps;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kohsuke.github.GHUpdateRepositoryBuilder;
import org.mockito.Mock;

import io.dangernoodle.grt.Repository.Settings;
import io.dangernoodle.grt.internal.GithubWorkflow;


public class SetRepositoryOptionsTest extends AbstractGithubWorkflowStepTest
{
    @Mock
    private GHUpdateRepositoryBuilder mockUpdateBuilder;

    @BeforeEach
    @Override
    public void beforeEach() throws Exception
    {
        super.beforeEach();

        when(mockUpdateBuilder.update()).thenReturn(mockGHRepository);
        when(mockGHRepository.updateRepository()).thenReturn(mockUpdateBuilder);
    }

    @Test
    public void testArchiveRepository() throws Exception
    {
        givenARepoToArchive();
        whenExecuteStep();
        thenRepositoryIsArchived();
        thenStatusIsSkip();
    }

    @Test
    public void testReArchiveIsNoOp() throws Exception
    {
        givenAnArchivedRepo();
        givenAGithubArchivedRepo();
        whenExecuteStep();
        thenGithubIsNoOp();
        thenStatusIsSkip();
    }

    @Test
    public void testSetRepositoryOptions() throws Exception
    {
        givenRepositoryOptions();
        givenGHRepositoryDefaults();
        whenExecuteStep();
        thenRepositoryIsUpdated();
        thenRepositorySettingsMatch();
        thenContextIsUpdated();
        thenStatusIsContinue();
    }

    @Test
    public void testSetRepositoryOptionsNoOp() throws Exception
    {
        givenGHRepositoryDefaults();
        whenExecuteStep();
        thenUpdateIsNotInvoked();
        thenContextIsUpdated();
        thenStatusIsContinue();
    }

    @Test
    public void testUnarchiveRepository() throws Exception
    {
        givenAGithubArchivedRepo();
        whenExecuteStep();
        thenGithubIsNoOp();
        thenStatusIsSkip();
    }

    @Override
    protected GithubWorkflow.Step createStep()
    {
        return new SetRepositoryOptions(mockClient);
    }

    private void givenAGithubArchivedRepo()
    {
        when(mockGHRepository.isArchived()).thenReturn(true);
    }

    private void givenAnArchivedRepo()
    {
        repoBuilder.setArchived(true);
    }

    private void givenARepoToArchive()
    {
        repoBuilder.setArchived(true);
    }

    private void givenGHRepositoryDefaults()
    {
        when(mockGHRepository.isAllowMergeCommit()).thenReturn(true);
        when(mockGHRepository.isAllowRebaseMerge()).thenReturn(true);
        when(mockGHRepository.isAllowSquashMerge()).thenReturn(true);
        when(mockGHRepository.isDeleteBranchOnMerge()).thenReturn(false);
        when(mockGHRepository.hasIssues()).thenReturn(true);
        when(mockGHRepository.hasWiki()).thenReturn(true);
    }

    private void givenRepositoryOptions()
    {
        repoBuilder.setDeleteBranchOnMerge(true)
                   .setDescription("description")
                   .setHomepage("homepage")
                   .setIssues(false)
                   .setMergeCommits(false)
                   .setRebaseMerge(false)
                   .setSquashMerge(false)
                   .setPrivate(true)
                   .setWiki(false);
    }

    private void thenContextIsUpdated()
    {
        verify(mockContext).add(mockGHRepository);
    }

    private void thenGithubIsNoOp()
    {
        verify(mockGHRepository).isArchived();
        verifyNoMoreInteractions(mockGHRepository);
    }

    private void thenRepositoryIsArchived() throws IOException
    {
        verify(mockGHRepository).isArchived();
        verify(mockGHRepository).archive();

        verifyNoMoreInteractions(mockGHRepository);
    }

    private void thenRepositoryIsUpdated() throws IOException
    {
        verify(mockUpdateBuilder).update();
    }

    private void thenRepositorySettingsMatch()
    {
        Settings settings = repository.getSettings();

        verify(mockUpdateBuilder).allowMergeCommit(settings.enableMergeCommits());
        verify(mockUpdateBuilder).allowRebaseMerge(settings.enableRebaseMerge());
        verify(mockUpdateBuilder).allowSquashMerge(settings.enableSquashMerge());
        verify(mockUpdateBuilder).deleteBranchOnMerge(settings.deleteBranchOnMerge());
        verify(mockUpdateBuilder).description(repository.getDescription());
        verify(mockUpdateBuilder).homepage(repository.getHomepage());
        verify(mockUpdateBuilder).issues(settings.enableIssues());
        verify(mockUpdateBuilder).private_(settings.isPrivate());
        verify(mockUpdateBuilder).wiki(settings.enableWiki());
    }

    private void thenUpdateIsNotInvoked()
    {
        verifyNoInteractions(mockUpdateBuilder);
    }
}
