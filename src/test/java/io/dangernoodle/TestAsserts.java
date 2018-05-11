package io.dangernoodle;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import io.dangernoodle.grt.Repository.Settings.Branches.Protection;


public final class TestAsserts
{
    public static void verifyCommitsAdminsDisabled(Protection protection)
    {
        assertThat(protection.getRequireSignedCommits(), equalTo(false));
        assertThat(protection.getIncludeAdministrators(), equalTo(false));
    }

    public static void verifyRequiredStatusChecksDisabled(Protection protection)
    {
        assertThat(protection.getRequiredChecks(), notNullValue());
        assertThat(protection.getRequiredChecks().isEnabled(), equalTo(false));
        assertThat(protection.getRequiredChecks().getRequireUpToDate(), equalTo(false));
        assertThat(protection.getRequiredChecks().getContexts().isEmpty(), equalTo(true));
    }

    public static void verifyRequiredStatusChecksEnabled(Protection protection, String name)
    {
        assertThat(protection.getRequiredChecks(), notNullValue());
        assertThat(protection.getRequiredChecks().isEnabled(), equalTo(true));
        assertThat(protection.getRequiredChecks().getRequireUpToDate(), equalTo(true));
        assertThat(protection.getRequiredChecks().getContexts(), contains(name));
    }

    public static void verifyRequireReviewsDisabled(Protection protection)
    {
        assertThat(protection.getRequireReviews(), notNullValue());
        assertThat(protection.getRequireReviews().isEnabled(), equalTo(false));
        assertThat(protection.getRequireReviews().getRequiredReviewers(), equalTo(1));
        assertThat(protection.getRequireReviews().getDismissStaleApprovals(), equalTo(false));
        assertThat(protection.getRequireReviews().getRequireCodeOwner(), equalTo(false));
        assertThat(protection.getRequireReviews().enableRestrictDismissals(), equalTo(false));
    }

    public static void verifyRequireReviewsEnabled(Protection protection)
    {
        assertThat(protection.getRequireReviews(), notNullValue());
        assertThat(protection.getRequireReviews().isEnabled(), equalTo(true));
        assertThat(protection.getRequireReviews().getRequiredReviewers(), equalTo(2));
        assertThat(protection.getRequireReviews().getDismissStaleApprovals(), equalTo(true));
        assertThat(protection.getRequireReviews().getRequireCodeOwner(), equalTo(true));
        assertThat(protection.getRequireReviews().enableRestrictDismissals(), equalTo(true));
    }
}
