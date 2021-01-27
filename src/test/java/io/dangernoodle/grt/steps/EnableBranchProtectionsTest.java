package io.dangernoodle.grt.steps;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kohsuke.github.GHBranch;
import org.kohsuke.github.GHBranchProtectionBuilder;
import org.kohsuke.github.GHTeam;
import org.kohsuke.github.GHUser;
import org.mockito.Mock;

import io.dangernoodle.grt.ext.statuschecks.RepositoryStatusCheckProvider;
import io.dangernoodle.grt.ext.statuschecks.StatusCheckProvider;
import io.dangernoodle.grt.internal.GithubWorkflow;


public class EnableBranchProtectionsTest extends AbstractGithubWorkflowStepTest
{
    private StatusCheckProvider factory;

    @Mock
    private GHBranch mockGHBranch;

    @Mock
    private GHBranchProtectionBuilder mockGHBuilder;

    @Mock
    private GHTeam mockGHTeam;

    @Mock
    private GHUser mockGHUser;

    @Override
    @BeforeEach
    public void beforeEach() throws Exception
    {
        factory = new RepositoryStatusCheckProvider();

        super.beforeEach();

        when(mockClient.getUser("user")).thenReturn(mockGHUser);
        when(mockClient.getTeam("org", "team")).thenReturn(mockGHTeam);

        when(mockGHBranch.getName()).thenReturn("master");
        when(mockGHBranch.enableProtection()).thenReturn(mockGHBuilder);
        when(mockGHRepository.getBranch("master")).thenReturn(mockGHBranch);
    }

    @Test
    public void testBranchDoesNotExist() throws Exception
    {
        givenABranchProtection();
        givenABranchThatDoesntExist();
        whenExecuteStep();
        thenBranchIsSkipped();
        thenStatusIsContinue();
    }

    @Test
    public void testDisableProtection() throws Exception
    {
        givenAProtectedBranch();
        whenExecuteStep();
        thenProtectionIsDisabled();
        thenStatusIsContinue();
    }

    @Test
    public void testDisableProtectionNoOp() throws Exception
    {
        givenANonProtectedBranch();
        whenExecuteStep();
        thenDisableProtectionIsNoOp();
        thenStatusIsContinue();
    }

    @Test
    public void testEnableAllProtections() throws Exception
    {
        givenRequiredReviews();
        givenReqiredChecks();
        givenRestrictedPushAccess();
        whenExecuteStep();
        thenBranchProtectionIsEnabled();
        thenRequiredReviewsAreEnabled();
        thenRequiredChecksAreEnabled();
        thenRestrictedPushAccessEnabled();
        thenStatusIsContinue();
    }

    @Test
    public void testEnableProtectionOnly() throws Exception
    {
        givenABranchProtection();
        whenExecuteStep();
        thenBranchProtectionIsEnabled();
        thenNoOtherProtectionsEnabled();
        thenStatusIsContinue();
    }

    @Test
    public void testEnablePushAccessOnly() throws Exception
    {
        givenRestrictedPushAccess();
        whenExecuteStep();
        thenBranchProtectionIsEnabled();
        thenRestrictedPushAccessEnabled();
        thenNoOtherProtectionsEnabled();
        thenStatusIsContinue();
    }

    @Test
    public void testEnableRequiredChecksOnly() throws Exception
    {
        givenReqiredChecks();
        whenExecuteStep();
        thenBranchProtectionIsEnabled();
        thenRequiredChecksAreEnabled();
        thenNoOtherProtectionsEnabled();
        thenStatusIsContinue();
    }

    @Test
    public void testEnableRequireReviewsOnly() throws Exception
    {
        givenRequiredReviews();
        whenExecuteStep();
        thenBranchProtectionIsEnabled();
        thenRequiredReviewsAreEnabled();
        thenNoOtherProtectionsEnabled();
        thenStatusIsContinue();
    }

    @Override
    protected GithubWorkflow.Step createStep()
    {
        return new EnableBranchProtections(mockClient, factory);
    }

    private void givenABranchProtection()
    {
        repoBuilder.enableBranchProtection("master");
    }

    private void givenABranchThatDoesntExist() throws IOException
    {
        when(mockGHRepository.getBranch("master")).thenReturn(null);
    }

    private void givenANonProtectedBranch()
    {
        when(mockGHBranch.isProtected()).thenReturn(false);
    }

    private void givenAProtectedBranch()
    {
        when(mockGHBranch.isProtected()).thenReturn(true);
    }

    private void givenReqiredChecks()
    {
        repoBuilder.requireBranchUpToDate("master", true);
        repoBuilder.addRequiredContext("master", "status-check");
    }

    private void givenRequiredReviews()
    {
        repoBuilder.requireReviews("master")
                   .requireCodeOwnerReview("master", true);
    }

    private void givenRestrictedPushAccess()
    {
        repoBuilder.restrictPushAccess("master")
                   .addTeamPushAccess("master", "team")
                   .addUserPushAccess("master", "user");
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

    private void thenDisableProtectionIsNoOp() throws IOException
    {
        verify(mockGHBranch, times(0)).disableProtection();
    }

    private void thenNoOtherProtectionsEnabled()
    {
        verifyNoMoreInteractions(mockGHBuilder);
    }

    private void thenProtectionIsDisabled() throws IOException
    {
        verify(mockGHBranch).disableProtection();
    }

    private void thenRequiredChecksAreEnabled()
    {
        verify(mockGHBuilder).requireBranchIsUpToDate(true);
        verify(mockGHBuilder).addRequiredChecks("status-check");
    }

    private void thenRequiredReviewsAreEnabled()
    {
        verify(mockGHBuilder).requireReviews();
        verify(mockGHBuilder).requireCodeOwnReviews(true);
    }

    private void thenRestrictedPushAccessEnabled() throws IOException
    {
        verify(mockGHBuilder).restrictPushAccess();
        verify(mockGHBuilder).teamPushAccess(mockGHTeam);
        verify(mockGHBuilder).userPushAccess(mockGHUser);
    }
}
