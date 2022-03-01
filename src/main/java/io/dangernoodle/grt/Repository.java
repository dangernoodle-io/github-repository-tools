package io.dangernoodle.grt;

import static io.dangernoodle.grt.Constants.REPOSITORY;
import static java.util.Optional.ofNullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import io.dangernoodle.grt.util.JsonTransformer.JsonArray;
import io.dangernoodle.grt.util.JsonTransformer.JsonObject;
import io.dangernoodle.grt.util.JsonTransformer.JsonObject.Deserializer;


/**
 * Represents a repository configuration.
 * 
 * @since 0.1.0
 */
public class Repository
{
    public static final Repository EMPTY = new Repository(JsonObject.NULL);

    private final JsonObject json;

    private final Map<String, JsonObject> plugins;

    private final Settings settings;

    private final Map<String, JsonArray> workflows;

    public Repository(JsonObject json)
    {
        this.json = json;

        this.plugins = buildPluginMap();
        this.workflows = buildWorkflowsMap();

        this.settings = new Settings(json.getJsonObject("settings"));
    }

    /**
     * @since 0.3.0
     */
    public String getDescription()
    {
        return json.getString("description");
    }

    /**
     * @since 0.4.0
     */
    public String getFullName()
    {
        return getOrganization() + "/" + getName();
    }

    /**
     * @since 0.3.0
     */
    public String getHomepage()
    {
        return json.getString("homepage");
    }

    /**
     * @since 0.3.0
     */
    public String getIgnoreTemplate()
    {
        return json.getString("ignoreTemplate");
    }

    /**
     * @since 0.3.0
     */
    public String getLicenseTemplate()
    {
        return json.getString("licenseTemplate");
    }

    public String getName()
    {
        return json.getString("name");
    }

    public String getOrganization()
    {
        return json.getString("organization");
    }

    public JsonObject getPlugin(String name)
    {
        return getPlugins().getOrDefault(name, JsonObject.NULL);
    }

    /**
     * @since 0.4.0
     */
    public Map<String, JsonObject> getPlugins()
    {
        return Optional.ofNullable(plugins)
                       .map(Collections::unmodifiableMap)
                       .orElse(Collections.emptyMap());
    }

    public Settings getSettings()
    {
        return settings;
    }

    @Deprecated
    public Collection<String> getWorkflow()
    {
        return getWorkflows(REPOSITORY);
    }

    /**
     * @since 0.9.0
     */
    public Map<String, JsonArray> getWorkflows()
    {
        return Optional.ofNullable(workflows)
                       .map(Collections::unmodifiableMap)
                       .orElse(Collections.emptyMap());
    }

    /**
     * @since 0.9.0
     */
    public Collection<String> getWorkflows(String command)
    {
        if (REPOSITORY.equals(command) && json.has("workflow"))
        {
            return json.getCollection("workflow", getWorkflow());
        }

        return json.getJsonObject("workflows")
                   .getCollection(command, Collections.emptyList());
    }

    /**
     * @since 0.9.0
     */
    public boolean isArchived()
    {
        return Optional.ofNullable(settings.isArchived())
                       .orElse(false);
    }

    /**
     * @since 0.4.0
     */
    @Override
    public String toString()
    {
        return json.prettyPrint();
    }

    private Map<String, JsonObject> buildPluginMap()
    {
        // only 'JsonObject's can be returned here
        return json.getMap("plugins", new Deserializer<JsonObject>()
        {
            @Override
            public JsonObject apply(JsonObject json)
            {
                return json;
            }
        });
    }

    private Map<String, JsonArray> buildWorkflowsMap()
    {
        // only 'JsonArray's can be returned here
        return json.getMap("workflows", new Deserializer<JsonArray>()
        {
            @Override
            public JsonArray apply(JsonArray json)
            {
                return json;
            }
        });
    }

    public static class Settings
    {
        private Branches branches;

        private final JsonObject json;

        private Settings(JsonObject json)
        {
            this.json = json;
            this.branches = new Branches(json.getJsonObject("branches"));
        }

        public Boolean autoInitialize()
        {
            return json.getBoolean("initialize");
        }

        /**
         * @since 0.6.0
         */
        public Boolean deleteBranchOnMerge()
        {
            return json.getBoolean("deleteBranchOnMerge");
        }

        /**
         * @since 0.3.0
         */
        public Boolean enableIssues()
        {
            return json.getBoolean("issues");
        }

        /**
         * @since 0.3.0
         */
        public Boolean enableMergeCommits()
        {
            return json.getBoolean("mergeCommits");
        }

        /**
         * @since 0.3.0
         */
        public Boolean enableRebaseMerge()
        {
            return json.getBoolean("rebaseMerge");
        }

        /**
         * @since 0.3.0
         */
        public Boolean enableSquashMerge()
        {
            return json.getBoolean("squashMerge");
        }

        /**
         * @since 0.3.0
         */
        public Boolean enableWiki()
        {
            return json.getBoolean("wiki");
        }

        public Branches getBranches()
        {
            return branches;
        }

        public Map<String, Permission> getCollaborators()
        {
            return json.getMap("collaborators", new Deserializer<Permission>()
            {
                @Override
                public Permission apply(String value)
                {
                    return Permission.valueOf(value);
                }
            });
        }

        public Map<String, Color> getLabels()
        {
            return json.getMap("labels", new Deserializer<Color>()
            {
                @Override
                public Color apply(String value)
                {
                    return Color.from(value);
                }
            });
        }

        public Map<String, Permission> getTeams()
        {
            return json.getMap("teams", new Deserializer<Permission>()
            {
                @Override
                public Permission apply(String value)
                {
                    return Permission.valueOf(value);
                }
            });
        }

        /**
         * @since 0.6.0
         */
        public Boolean isArchived()
        {
            return json.getBoolean("archived");
        }

        public Boolean isPrivate()
        {
            return json.getBoolean("hidden");
        }

        public static class AccessRestrictions
        {
            private final JsonObject json;

            private AccessRestrictions(JsonObject json)
            {
                this.json = json;
            }

            public Collection<String> getTeams()
            {
                return json.getCollection("teams");
            }

            public Collection<String> getUsers()
            {
                return json.getCollection("users");
            }

            public boolean hasTeams()
            {
                return json.has("teams");
            }

            public boolean hasUsers()
            {
                return json.has("users");
            }

            public boolean isEnabled()
            {
                return json.isNotNull();
            }

        }

        public static class Branches
        {
            private final JsonObject json;

            private final Map<String, Protection> protections;

            private Branches(JsonObject json)
            {
                this.json = json;
                this.protections = getProtections();
            }

            public String getDefault()
            {
                return json.getString("primary");
            }

            public Collection<String> getOther()
            {
                return json.getCollection("other");
            }

            public Protection getProtection(String branch)
            {
                return protections.getOrDefault(branch, Protection.NULL);
            }

            public boolean hasProtection(String branch)
            {
                return protections.containsKey(branch);
            }

            private Map<String, Protection> getProtections()
            {
                return ofNullable(json.getMap("protections", new Deserializer<Protection>()
                {
                    @Override
                    public Protection apply(JsonObject value)
                    {
                        return new Protection(value);
                    }

                    @Override
                    public Protection apply(String value)
                    {
                        return Protection.NULL;
                    }
                })).orElse(Collections.emptyMap());
            }

            public static class Protection
            {
                public static final Protection NULL = new Protection(JsonObject.NULL);

                private final JsonObject json;

                private final AccessRestrictions pushAccess;

                private final RequiredChecks requiredChecks;

                private final RequireReviews requiredReviews;

                private Protection(JsonObject json)
                {
                    this.json = json;

                    this.requiredReviews = new RequireReviews(json.getJsonObject("requireReviews"));
                    this.requiredChecks = new RequiredChecks(json.getJsonObject("requiredStatusChecks"));
                    this.pushAccess = new AccessRestrictions(json.getJsonObject("pushAccess"));
                }

                public Boolean getIncludeAdministrators()
                {
                    return json.getBoolean("includeAdministrators");
                }

                public AccessRestrictions getPushAccess()
                {
                    return pushAccess;
                }

                public RequiredChecks getRequiredChecks()
                {
                    return requiredChecks;
                }

                public RequireReviews getRequireReviews()
                {
                    return requiredReviews;
                }

                public Boolean getRequireSignedCommits()
                {
                    return json.getBoolean("requireSignedCommits");
                }

                public boolean hasRequiredChecks()
                {
                    return json.has("requiredStatusChecks");
                }

                public boolean hasRequireReviews()
                {
                    return json.has("requireReviews");
                }

                public boolean hasRestrictedPushAccess()
                {
                    return pushAccess.isEnabled();
                }

                public boolean isEnabled()
                {
                    return json.isNotNull();
                }

                public static class RequiredChecks
                {
                    private final JsonObject json;

                    private RequiredChecks(JsonObject json)
                    {
                        this.json = json;
                    }

                    public Collection<String> getContexts()
                    {
                        return json.getCollection("contexts");
                    }

                    public Boolean getRequireUpToDate()
                    {
                        return json.getBoolean("requireUpToDate");
                    }

                    public boolean hasContexts()
                    {
                        return json.has("contexts");
                    }

                    public boolean isEnabled()
                    {
                        return json.isNotNull();
                    }
                }

                public static class RequireReviews
                {
                    private final AccessRestrictions dismissals;

                    private final JsonObject json;

                    private RequireReviews(JsonObject json)
                    {
                        this.json = json;
                        this.dismissals = new AccessRestrictions(json.getJsonObject("restrictDismissals"));
                    }

                    public AccessRestrictions getDismissalRestrictions()
                    {
                        return dismissals;
                    }

                    public Boolean getDismissStaleApprovals()
                    {
                        return json.getBoolean("dismissStaleApprovals");
                    }

                    public Boolean getRequireCodeOwner()
                    {
                        return json.getBoolean("requireCodeOwner");
                    }

                    public Integer getRequiredReviewers()
                    {
                        return json.getInteger("requiredReviewers");
                    }

                    public boolean hasDismissalRestrictions()
                    {
                        return json.has("restrictDismissals");
                    }

                    public boolean isEnabled()
                    {
                        return json.isNotNull();
                    }
                }
            }
        }

        public static class Color
        {
            private final String color;

            private Color(String color)
            {
                this.color = color;
            }

            @Override
            public boolean equals(Object obj)
            {
                if (obj == null || getClass() != obj.getClass())
                {
                    return false;
                }

                return Objects.equals(this.color, ((Color) obj).color);
            }

            @Override
            public int hashCode()
            {
                return Objects.hash(color);
            }

            @Override
            public String toString()
            {
                return color;
            }

            public static Color from(String color)
            {
                // TODO: validate color is valid hex as well...
                color = color.startsWith("#") ? color.substring(1) : color;

                return new Color(color);
            }
        }

        public static enum Permission
        {
            admin,
            developer,
            read,
            write;
        }
    }
}
