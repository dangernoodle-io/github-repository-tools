package io.dangernoodle.grt.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import io.dangernoodle.grt.Repository;
import io.dangernoodle.grt.Repository.Settings.Color;
import io.dangernoodle.grt.Repository.Settings.Permission;
import io.dangernoodle.grt.utils.JsonTransformer.JsonObject;


public class RepositoryBuilder
{
    private final Map<String, Object> repository;

    private final JsonTransformer transformer;

    public RepositoryBuilder(JsonTransformer transformer)
    {
        this.transformer = transformer;
        this.repository = createEmptyMap();
    }

    public RepositoryBuilder addCollaborator(String user, Permission permission)
    {
        collaborators().put(user, permission);
        return this;
    }

    public RepositoryBuilder addCollaborators()
    {
        collaborators();
        return this;
    }

    public RepositoryBuilder addCollaborators(Map<String, Permission> collaborators)
    {
        collaborators().putAll(collaborators);
        return this;
    }

    public RepositoryBuilder addLabel(String name, Color color)
    {
        labels().put(name, color.toString());
        return this;
    }

    public void addLabels()
    {
        labels();
    }

    public void addLabels(Map<String, Color> labels)
    {
        labels().putAll(labels);
    }

    public RepositoryBuilder addOtherBranch(String branch)
    {
        computeCollectionIfAbsent("other", branches()).add(branch);
        return this;
    }

    public RepositoryBuilder addOtherBranches(Collection<String> branches)
    {
        computeCollectionIfAbsent("other", branches()).addAll(branches);
        return this;
    }

    public RepositoryBuilder addPlugin(String key, Object plugin)
    {
        computeMapIfAbsent("plugins", repository).put(key, plugin);
        return this;
    }

    public RepositoryBuilder addRequiredContext(String branch, String context)
    {
        computeCollectionIfAbsent("contexts", statusChecks(branch)).add(context);
        return this;
    }

    public RepositoryBuilder addTeam(String team, Permission permission)
    {
        teams().put(team, permission);
        return this;
    }

    public RepositoryBuilder addTeamPushAccess(String branch, String team)
    {
        addRestriction("teams", "pushAccess", protections(branch), team);
        return this;
    }

    public RepositoryBuilder addTeamReviewDismisser(String branch, String team)
    {
        addRestriction("teams", "restrictDismissals", reviews(branch), team);
        return this;
    }

    public RepositoryBuilder addTeams()
    {
        teams();
        return this;
    }

    public RepositoryBuilder addTeams(Map<String, Permission> teams)
    {
        teams().putAll(teams);
        return this;
    }

    public RepositoryBuilder addUserPushAccess(String branch, String user)
    {
        addRestriction("users", "pushAccess", protections(branch), user);
        return this;
    }

    public RepositoryBuilder addUserReviewDismisser(String branch, String user)
    {
        addRestriction("users", "restrictDismissals", reviews(branch), user);
        return this;
    }

    public RepositoryBuilder addWorkflow(String workflow)
    {
        computeCollectionIfAbsent("workflow", repository).add(workflow);
        return this;
    }

    public Repository build()
    {
        JsonObject json = transformer.serialize(repository);
        // System.out.println(json.prettyPrint());

        return new Repository(json);
    }

    public RepositoryBuilder disableBranchProtection(String branch)
    {
        computeMapIfAbsent("protections", branches()).put(branch, JsonTransformer.NULL);
        return this;
    }

    public RepositoryBuilder dismissStaleApprovals(String branch, boolean bool)
    {
        reviews(branch).put("dismissStaleApprovals", bool);
        return this;
    }

    public RepositoryBuilder enableBranchProtection(String branch)
    {
        protections(branch);
        return this;
    }

    public RepositoryBuilder enforceForAdminstrators(String branch, boolean bool)
    {
        protections(branch).put("includeAdministrators", bool);
        return this;
    }

    public RepositoryBuilder requireBranchUpToDate(String branch, boolean bool)
    {
        statusChecks(branch).put("requireUpToDate", bool);
        return this;
    }

    public RepositoryBuilder requireCodeOwnerReview(String branch, boolean bool)
    {
        reviews(branch).put("requireCodeOwner", bool);
        return this;
    }

    public RepositoryBuilder requiredReviewers(String branch, int required)
    {
        reviews(branch).put("requiredReviewers", required);
        return this;
    }

    public RepositoryBuilder requireReviews(String branch)
    {
        reviews(branch);
        return this;
    }

    public RepositoryBuilder requireSignedCommits(String branch, boolean bool)
    {
        protections(branch).put("requireSignedCommits", bool);
        return this;
    }

    public RepositoryBuilder restrictPushAccess(String branch)
    {
        pushAccess(branch);
        return this;
    }

    public RepositoryBuilder setDescription(String description)
    {
        repository.put("description", description);
        return this;
    }

    public RepositoryBuilder setHomepage(String homepage)
    {
        repository.put("homepage", homepage);
        return this;
    }

    public RepositoryBuilder setInitialize(boolean bool)
    {
        settings().put("initialize", bool);
        return this;
    }

    public RepositoryBuilder setName(String name)
    {
        repository.put("name", name);
        return this;
    }

    public RepositoryBuilder setOrganization(String organization)
    {
        repository.put("organization", organization);
        return this;
    }

    public RepositoryBuilder setPrimaryBranch(String branch)
    {
        branches().put("primary", branch);
        return this;
    }

    public RepositoryBuilder setPrivate(boolean bool)
    {
        settings().put("hidden", bool);
        return this;
    }

    private void addRestriction(String key, String parentKey, Map<String, Object> parent, String team)
    {
        computeCollectionIfAbsent(key, computeMapIfAbsent(parentKey, parent)).add(team);
    }

    private Map<String, Object> branches()
    {
        Map<String, Object> branches = computeMapIfAbsent("branches", settings());
        return branches;
    }

    private Map<String, Object> collaborators()
    {
        return computeMapIfAbsent("collaborators", settings());
    }

    @SuppressWarnings("unchecked")
    private Collection<String> computeCollectionIfAbsent(String key, Map<String, Object> parent)
    {
        return (Collection<String>) parent.computeIfAbsent(key, k -> new ArrayList<>());
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> computeMapIfAbsent(String key, Map<String, Object> parent)
    {
        return (Map<String, Object>) parent.computeIfAbsent(key, k -> createEmptyMap());
    }

    private <K, V> Map<K, V> createEmptyMap()
    {
        return new HashMap<>();
    }

    private Map<String, Object> labels()
    {
        return computeMapIfAbsent("labels", settings());
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> protections(String branch)
    {
        return (Map<String, Object>) computeMapIfAbsent("protections", branches()).computeIfAbsent(branch,
                k -> createEmptyMap());
    }

    private Map<String, Object> pushAccess(String branch)
    {
        return computeMapIfAbsent("pushAccess", protections(branch));
    }

    private Map<String, Object> reviews(String branch)
    {
        return computeMapIfAbsent("requireReviews", protections(branch));
    }

    private Map<String, Object> settings()
    {
        return computeMapIfAbsent("settings", repository);
    }

    private Map<String, Object> statusChecks(String branch)
    {
        return computeMapIfAbsent("requiredStatusChecks", protections(branch));
    }

    private Map<String, Object> teams()
    {
        return computeMapIfAbsent("teams", settings());
    }
}
