package io.dangernoodle.grt.internal;

import static io.dangernoodle.TestAsserts.verifyBranchProtectionIsDisabled;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.io.IOException;
import java.util.Collections;

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
import io.dangernoodle.grt.json.JsonTransformer;
import io.dangernoodle.grt.json.JsonTransformer.JsonObject;


public class RepositoryBuilderTest
{
    private Repository actual;

    private RepositoryBuilder builder;

    private Repository expected;

    @BeforeEach
    public void before()
    {
        builder = new RepositoryBuilder();
    }

    @Test
    public void testBuildCompleteRepository() throws Exception
    {
        givenARepository();
        whenBuildRepository();
        thenRepositoriesMatch();
    }

    @Test
    public void testDisableBranchProtection()
    {
        givenADisabledBranchProtection();
        whenBuildRepository();
        thenBranchProtectionIsNull();
    }
    
    private void givenADisabledBranchProtection()
    {
        builder.disableBranchProtection("master");
    }

    private void givenARepository() throws IOException
    {
        expected = Repository.load(TestFiles.mockRepository.getFile());

        builder.setName("grt-test-repository")
               .setOrganization("dangernoodle-io")
               .setInitialize(true)
               .setPrivate(true)
               .addLabel("label", Color.from("#00000"))
               .addTeam("admin", Permission.admin)
               .addCollaborator("user", Permission.read)
               .setPrimaryBranch("master")
               .addOtherBranch("other")
               .requireSignedCommits("master", true)
               .enforceForAdminstrators("master", true)
               .requireReviews("master")
               .requiredReviewers("master", 2)
               .dismissStaleApprovals("master", true)
               .requireCodeOwnerReview("master", true)
               .addTeamReviewDismisser("master", "write")
               .addUserReviewDismisser("master", "user")
               .requireBranchUpToDate("master", true)
               .addRequiredContext("master", "grt-test-repository")
               .restrictPushAccess("master")
               .addTeamPushAccess("master", "write")
               .addUserPushAccess("master", "user")
               .addPlugin("jenkins", toJson("container", "maven"))
               .addWorkflow("jenkins");
    }
    
    private JsonObject toJson(String key, String value)
    {
        return JsonTransformer.serialize(Collections.singletonMap(key, value));
    }
    
    private void thenBranchProtectionIsNull()
    {
        verifyBranchProtectionIsDisabled(actual, "master");
    }

    private void thenRepositoriesMatch()
    {
        assertThat(actual.getName(), equalTo(expected.getName()));
        assertThat(actual.getOrganization(), equalTo(expected.getOrganization()));

        assertThat(((JsonObject) actual.getPlugin("jenkins")).getString("container"), equalTo("maven"));
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

        assertThat(aProtection.enableRestrictedPushAccess(), equalTo(eProtection.enableRestrictedPushAccess()));
        assertThat(aProtection.getPushTeams().containsAll(eProtection.getPushTeams()), equalTo(true));
        assertThat(aProtection.getPushUsers().containsAll(eProtection.getPushUsers()), equalTo(true));
    }

    private void whenBuildRepository()
    {
        actual = builder.build();
    }
}
