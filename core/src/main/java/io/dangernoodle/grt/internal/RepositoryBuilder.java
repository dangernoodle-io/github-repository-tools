package io.dangernoodle.grt.internal;

import static io.dangernoodle.grt.json.DefaultJsonTransformer.transformer;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

import io.dangernoodle.grt.Repository;
import io.dangernoodle.grt.Repository.Color;
import io.dangernoodle.grt.Repository.Permission;


public class RepositoryBuilder
{
    private final Map<String, Object> repository;

    public RepositoryBuilder()
    {
        this.repository = createEmptyMap();
    }

    public RepositoryBuilder addCollaborator(String user, Permission permission)
    {
        computeMapIfAbsent("collaborators", settings()).put(user, permission);
        return this;
    }

    public RepositoryBuilder addLabel(String name, Color color)
    {
        computeMapIfAbsent("labels", settings()).put(name, color.toString());
        return this;
    }

    public RepositoryBuilder addOtherBranch(String branch)
    {
        computeCollectionIfAbsent("other", branches()).add(branch);
        return this;
    }

    public RepositoryBuilder addRequiredContext(String branch, String context)
    {
        computeCollectionIfAbsent("contexts", statusChecks(branch)).add(context);
        return this;
    }

    public RepositoryBuilder addTeam(String team, Permission permission)
    {
        computeMapIfAbsent("teams", settings()).put(team, permission);
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

    public Repository build()
    {
        String json = transformer.serialize(repository);
        //System.out.println(transformer.prettyPrint(json));

        return transformer.deserialize(json, Repository.class);
    }

    public RepositoryBuilder dismissStaleApprovals(String branch, boolean bool)
    {
        reviews(branch).put("dismissStaleApprovals", bool);
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

    public String toJson()
    {
        String json = transformer.serialize(repository);
        System.out.println(transformer.prettyPrint(json));

        return json;
    }

    private void addRestriction(String key, String parentKey, Map<String, Object> parent, String team)
    {
        computeCollectionIfAbsent(key, computeMapIfAbsent(parentKey, parent)).add(team);
    }

    private Map<String, Object> branches()
    {
        return computeMapIfAbsent("branches", settings());
    }

    @SuppressWarnings("unchecked")
    private Collection<String> computeCollectionIfAbsent(String key, Map<String, Object> parent)
    {
        return (Collection<String>) parent.computeIfAbsent(key, k -> new HashSet<>());
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> computeMapIfAbsent(String key, Map<String, Object> parent)
    {
        return (Map<String, Object>) parent.computeIfAbsent(key, k -> createEmptyMap());
    }

    private <K, V> Map<K, V> createEmptyMap()
    {
        return new LinkedHashMap<>();
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
}
