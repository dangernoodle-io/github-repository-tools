package io.dangernoodle.grt.workflow.step;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kohsuke.github.GHBranch;
import org.kohsuke.github.GHBranchProtection;
import org.kohsuke.github.GHBranchProtectionBuilder;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHTeam;
import org.kohsuke.github.GHUser;
import org.mockito.Mock;

import io.dangernoodle.grt.StatusCheck;
import io.dangernoodle.grt.statuscheck.RepositoryStatusCheck;


public class EnableBranchProtectionsTest extends AbstractGithubWorkflowStepTest
{
    private String branchName;

    private StatusCheck factory;

    @Mock
    private GHBranch mockGHBranch;

    @Mock
    private GHBranchProtectionBuilder mockGHBuilder;

    @Mock
    private GHTeam mockGHTeam;

    @Mock
    private GHUser mockGHUser;

    @Mock
    private GHBranchProtection mockProtection;

    @Override
    @BeforeEach
    public void beforeEach() throws Exception
    {
        branchName = "master";
        factory = new RepositoryStatusCheck();

        super.beforeEach();

        when(mockClient.getUser("user")).thenReturn(mockGHUser);
        when(mockClient.getTeam("org", "team")).thenReturn(mockGHTeam);

        when(mockGHBranch.getName()).thenReturn(branchName);
        when(mockGHBranch.enableProtection()).thenReturn(mockGHBuilder);

        when(mockGHBuilder.enable()).thenReturn(mockProtection);
        when(mockGHBuilder.includeAdmins(false)).thenReturn(mockGHBuilder);

        when(mockGHRepository.getBranch(branchName)).thenReturn(mockGHBranch);
        when(mockContext.get(GHRepository.class)).thenReturn(mockGHRepository);
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
        givenRequireAdmins();
        givenRequireSignedCommits();
        whenExecuteStep();
        thenBranchProtectionIsEnabled();
        thenRequiredReviewsAreEnabled();
        thenRequiredChecksAreEnabled();
        thenRestrictedPushAccessEnabled();
        thenAdminsAreRequired();
        thenSignedCommitsAreRequired();
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
    protected AbstractGithubStep createStep()
    {
        return new EnableBranchProtections(mockClient, factory);
    }

    private void givenABranchProtection()
    {
        repoBuilder.enableBranchProtection(branchName);
    }

    private void givenABranchThatDoesntExist() throws IOException
    {
        when(mockGHRepository.getBranch(branchName)).thenReturn(null);
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
        repoBuilder.requireBranchUpToDate(branchName, true);
        repoBuilder.addRequiredContext(branchName, "status-check");
    }

    private void givenRequireAdmins()
    {
        repoBuilder.enforceForAdminstrators(branchName, true);
    }

    private void givenRequiredReviews()
    {
        repoBuilder.requireReviews(branchName)
                   .requireCodeOwnerReview(branchName, true)
                   .requiredReviewers(branchName, 2)
                   .dismissStaleApprovals(branchName, true);
    }

    private void givenRequireSignedCommits()
    {
        repoBuilder.requireSignedCommits(branchName, true);
    }

    private void givenRestrictedPushAccess()
    {
        repoBuilder.restrictPushAccess(branchName)
                   .addTeamPushAccess(branchName, "team")
                   .addUserPushAccess(branchName, "user");
    }

    private void thenAdminsAreRequired()
    {
        verify(mockGHBuilder).includeAdmins();
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
        verify(mockGHBuilder).requiredReviewers(2);
        verify(mockGHBuilder).requireCodeOwnReviews(true);
        verify(mockGHBuilder).dismissStaleReviews(true);
    }

    private void thenRestrictedPushAccessEnabled()
    {
        verify(mockGHBuilder).restrictPushAccess();
        verify(mockGHBuilder).teamPushAccess(mockGHTeam);
        verify(mockGHBuilder).userPushAccess(mockGHUser);
    }

    private void thenSignedCommitsAreRequired() throws IOException
    {
        verify(mockProtection).enabledSignedCommits();
    }
}
