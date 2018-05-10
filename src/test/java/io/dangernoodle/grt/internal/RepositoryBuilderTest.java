package io.dangernoodle.grt.internal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.dangernoodle.TestFiles;
import io.dangernoodle.grt.Repository;
import io.dangernoodle.grt.Repository.Color;
import io.dangernoodle.grt.Repository.Permission;
import io.dangernoodle.grt.Repository.Settings;
import io.dangernoodle.grt.Repository.Settings.Branches;
import io.dangernoodle.grt.Repository.Settings.Branches.Protection;
import io.dangernoodle.grt.Repository.Settings.Branches.Protection.RequireReviews;
import io.dangernoodle.grt.Repository.Settings.Branches.Protection.RequiredChecks;
import io.dangernoodle.grt.internal.RepositoryBuilder;


public class RepositoryBuilderTest
{
    private Repository expected;

    private Repository actual;

    private RepositoryBuilder builder;

    @BeforeEach
    public void before()
    {
        builder = new RepositoryBuilder();
    }

    @Test
    public void testBuildingComplete()
    {
        givenARepository();
        whenSerializeJson();
        thenRepositoriesMatch();
    }

    private void thenRepositoriesMatch()
    {
        assertThat(actual.getName(), equalTo(expected.getName()));
        assertThat(actual.getOrganization(), equalTo(expected.getOrganization()));

        assertThat(actual.getPlugins().get("jenkins"), equalTo(expected.getPlugins().get("jenkins")));
        assertThat(actual.getWorkflow().containsAll(expected.getWorkflow()), equalTo(true));
        
        Settings aSettings = actual.getSettings();
        Settings eSettings = expected.getSettings();

        assertThat(aSettings.autoInitialize(), equalTo(eSettings.autoInitialize()));
        assertThat(aSettings.isPrivate(), equalTo(eSettings.isPrivate()));
        assertThat(aSettings.getLabels(), equalTo(eSettings.getLabels()));
        assertThat(aSettings.getTeams(), equalTo(eSettings.getTeams()));
        assertThat(aSettings.getCollaborators(), equalTo(eSettings.getCollaborators()));

        Branches aBranches = aSettings.getBranches();
        Branches eBranches = eSettings.getBranches();

        assertThat(aBranches.getDefault(), equalTo(eBranches.getDefault()));
        assertThat(aBranches.getOther().containsAll(eBranches.getOther()), equalTo(true));

        Protection aProtection = aBranches.getProtection("master");
        Protection eProtection = eBranches.getProtection("master");

        assertThat(aProtection.getRequireSignedCommits(), equalTo(eProtection.getRequireSignedCommits()));
        assertThat(aProtection.getIncludeAdministrators(), equalTo(eProtection.getIncludeAdministrators()));

        RequireReviews aReviews = aProtection.getRequireReviews();
        RequireReviews eReviews = eProtection.getRequireReviews();

        assertThat(aReviews.getRequiredReviewers(), equalTo(eReviews.getRequiredReviewers()));
        assertThat(aReviews.getDismissStaleApprovals(), equalTo(eReviews.getDismissStaleApprovals()));
        assertThat(aReviews.getRequireCodeOwner(), equalTo(eReviews.getRequireCodeOwner()));

        assertThat(aReviews.enableRestrictDismissals(), equalTo(eReviews.enableRestrictDismissals()));
        assertThat(aReviews.getDismissalTeams().containsAll(eReviews.getDismissalTeams()), equalTo(true));
        assertThat(aReviews.getDismissalUsers().containsAll(eReviews.getDismissalUsers()), equalTo(true));

        RequiredChecks aChecks = aProtection.getRequiredChecks();
        RequiredChecks eChecks = eProtection.getRequiredChecks();

        assertThat(aChecks.getRequireUpToDate(), equalTo(eChecks.getRequireUpToDate()));
        assertThat(aChecks.getContexts().containsAll(eChecks.getContexts()), equalTo(true));

        assertThat(aProtection.enablePushAccess(), equalTo(eProtection.enablePushAccess()));
        assertThat(aProtection.getPushTeams().containsAll(eProtection.getPushTeams()), equalTo(true));
        assertThat(aProtection.getPushUsers().containsAll(eProtection.getPushUsers()), equalTo(true));
    }

    private void givenARepository()
    {
        expected = TestFiles.mockRepository.parseIntoObject(Repository.class);

        builder.setName("grt-test-repository")
               .setOrganization("dangernoodle-io")
               .setInitialize(true)
               .setPrivate(true)
               .addLabel("label", Color.from("#00000"))
               .addTeam("read", Permission.read)
               .addTeam("write", Permission.write)
               .addTeam("admin", Permission.admin)
               .addCollaborator("user", Permission.read)
               .setPrimaryBranch("master")
               .addOtherBranch("other")
               .requireSignedCommits("master", true)
               .enforceForAdminstrators("master", true)
               .requireReviews("master")
               .requiredReviewers("master", 1)
               .dismissStaleApprovals("master", true)
               .requireCodeOwnerReview("master", true)
               .addTeamReviewDismisser("master", "write")
               .addUserReviewDismisser("master", "user")
               .requireBranchUpToDate("master", true)
               .addRequiredContext("master", "grt-test-repository")
               .restrictPushAccess("master")
               .addTeamPushAccess("master", "write")
               .addUserPushAccess("master", "user")
               .addPlugin("jenkins", "[{\"container\": \"maven\"}]")
               .addWorkflow("jenkins");
    }

    private void whenSerializeJson()
    {
        actual = builder.build();
    }
}
