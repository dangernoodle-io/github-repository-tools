package io.dangernoodle.grt.steps;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.kohsuke.github.GHOrganization;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHUser;

import io.dangernoodle.grt.GithubClient;
import io.dangernoodle.grt.Repository;
import io.dangernoodle.grt.Repository.Permission;
import io.dangernoodle.grt.Workflow.Context;
import io.dangernoodle.grt.internal.GithubWorkflow;


public class AddTeamsAndCollaborators extends GithubWorkflow.Step
{
    public AddTeamsAndCollaborators(GithubClient client)
    {
        super(client);
    }

    @Override
    public void execute(Repository repository, Context context) throws IOException
    {
        GHRepository ghRepo = context.get(GHRepository.class);
        Map<String, Permission> users = repository.getSettings().getCollaborators();

        if (context.isOrg())
        {
            addUsers(ghRepo, users, true);
            addTeams(repository.getOrganization(), ghRepo, repository.getSettings().getTeams());
        }
        else
        {
            addUsers(ghRepo, users, false);
        }
    }

    private void addTeams(String organization, GHRepository ghRepo, Map<String, Permission> teams) throws IOException
    {
        for (Entry<String, Permission> entry : teams.entrySet())
        {
            String team = entry.getKey();
            Permission perm = entry.getValue();

            client.getTeam(organization, team)
                  .add(ghRepo, mapToOrgPermission(perm));

            logger.info("granted team [{} / {}] repository access", team, perm);
        }
    }

    private void addUsers(GHRepository ghRepo, Map<String, Permission> users, boolean isOrg) throws IOException
    {
        for (Entry<String, Permission> entry : users.entrySet())
        {
            String user = entry.getKey();
            Permission perm = entry.getValue();

            GHUser ghUser = client.getUser(entry.getKey());

            if (isOrg)
            {
                // TODO: allow for permission to be set, requires change to github-api
                ghRepo.addCollaborators(ghUser);
            }
            else
            {
                // this is the permission set by github no matter what the user specifies
                perm = Permission.write;
                ghRepo.addCollaborators(ghUser);
            }

            logger.info("granted user [{} / {}] repository access", user, perm);
        }
    }

    private GHOrganization.Permission mapToOrgPermission(Permission permission)
    {
        switch (permission)
        {
            case admin:
                return GHOrganization.Permission.ADMIN;
            case write:
                return GHOrganization.Permission.PUSH;
            default:
                return GHOrganization.Permission.PULL;
        }
    }
}
