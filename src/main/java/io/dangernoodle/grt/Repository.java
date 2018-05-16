package io.dangernoodle.grt;

import static io.dangernoodle.grt.json.JsonTransformer.deserialize;
import static java.util.Optional.ofNullable;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import io.dangernoodle.grt.json.JsonTransformer.JsonArray;
import io.dangernoodle.grt.json.JsonTransformer.JsonObject;
import io.dangernoodle.grt.json.JsonTransformer.JsonObject.Deserializer;


/**
 * Represents a repository configuration.
 * 
 * @since 0.1.0
 */
public class Repository
{
    public static final String GITHUB = "github";

    private final JsonObject json;

    private final Map<String, Object> plugins;

    private final Settings settings;

    private Repository(JsonObject json)
    {
        this.json = json;

        this.plugins = buildPluginMap();
        this.settings = new Settings(json.getJsonObject("settings"));
    }

    public String getName()
    {
        return json.getString("name");
    }

    public String getOrganization()
    {
        return json.getString("organization");
    }

    @SuppressWarnings("unchecked")
    public <T> T getPlugin(String name)
    {
        return (T) getPlugins().get(name);
    }

    public Map<String, Object> getPlugins()
    {
        return plugins;
    }

    public Settings getSettings()
    {
        return settings;
    }

    public Collection<String> getWorkflow()
    {
        return json.getCollection("workflow");
    }

    private Map<String, Object> buildPluginMap()
    {
        return json.getMap("plugins", new Deserializer<Object>()
        {
            @Override
            public Object apply(JsonArray json)
            {
                return json;
            }

            @Override
            public Object apply(JsonObject json)
            {
                return json;
            }
        });
    }

    public static Repository load(File file) throws IOException
    {
        return load(deserialize(file));
    }

    public static Repository load(JsonObject json)
    {
        return new Repository(json);
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
            read,
            write;
        }
    }
}
