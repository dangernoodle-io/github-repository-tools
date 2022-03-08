package io.dangernoodle.grt.workflow.step;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.kohsuke.github.GHOrganization;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHTeam;
import org.kohsuke.github.GHUser;

import io.dangernoodle.grt.Repository;
import io.dangernoodle.grt.Repository.Settings.Permission;
import io.dangernoodle.grt.Workflow.Context;
import io.dangernoodle.grt.Workflow.Status;
import io.dangernoodle.grt.util.GithubClient;


public class AddTeamsAndCollaborators extends AbstractGithubStep
{
    public AddTeamsAndCollaborators(GithubClient client)
    {
        super(client);
    }

    @Override
    public Status execute(Repository repository, Context context) throws IOException
    {
        GHRepository ghRepo = context.getGHRepository();
        Map<String, Permission> users = repository.getSettings().getCollaborators();

        if ("Organization".equals(ghRepo.getOwner().getType()))
        {
            addUsers(ghRepo, users, true);
            addTeams(repository.getOrganization(), ghRepo, repository.getSettings().getTeams());
        }
        else
        {
            addUsers(ghRepo, users, false);
        }

        return Status.CONTINUE;
    }

    private void addTeams(String organization, GHRepository ghRepo, Map<String, Permission> teams) throws IOException
    {
        for (Entry<String, Permission> entry : teams.entrySet())
        {
            String team = entry.getKey();
            Permission perm = entry.getValue();

            GHTeam ghTeam = client.getTeam(organization, team);
            if (ghTeam == null)
            {
                logger.warn("failed to find team using slug [{}]", team);
                continue;
            }

            ghTeam.add(ghRepo, mapToOrgPermission(perm));
            logger.info("granted team [{} / {}] repository access", team, perm);
        }
    }

    private void addUsers(GHRepository ghRepo, Map<String, Permission> users, boolean isOrg) throws IOException
    {
        for (Entry<String, Permission> entry : users.entrySet())
        {
            String user = entry.getKey();
            Permission perm = entry.getValue();

            GHUser ghUser = client.getUser(user);

            if (ghUser == null)
            {
                logger.warn("failed to find user [{}]", user);
                continue;
            }

            if (isOrg)
            {
                // TODO: allow for permission to be set, requires change to github-api
                ghRepo.addCollaborators(ghUser);
            }
            else
            {
                // this is the permission set by github no matter what the user specifies
                perm = new Permission("write");
                ghRepo.addCollaborators(ghUser);
            }

            logger.info("granted user [{} / {}] repository access", user, perm);
        }
    }

    private GHOrganization.Permission mapToOrgPermission(Permission permission)
    {
        String value = permission.toString();

        switch (value)
        {
            case "admin":
                return GHOrganization.Permission.ADMIN;
            case "read":
            case "pull":
                return GHOrganization.Permission.PULL;
            case "push":
            case "write":
                return GHOrganization.Permission.PUSH;
            case "triage":
                return GHOrganization.Permission.TRIAGE;
            default:
                throw new IllegalStateException("failed to find permission mapping");
                //return new GHOrganization.RolePermission(value);
        }
    }
}
