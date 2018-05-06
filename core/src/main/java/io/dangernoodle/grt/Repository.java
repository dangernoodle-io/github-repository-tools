package io.dangernoodle.grt;

import static io.dangernoodle.grt.json.DefaultJsonTransformer.transformer;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;


public class Repository
{
    private String name;

    private String organization;

    private Plugins plugins;

    private Settings settings;

    private Collection<String> workflow;

    public String getName()
    {
        return name;
    }

    public String getOrganization()
    {
        return organization;
    }

    public Map<String, String> getPlugins()
    {
        return plugins == null ? Collections.emptyMap() : plugins.plugins;
    }

    public Settings getSettings()
    {
        return settings == null ? Settings.NULL : settings;
    }

    public Collection<String> getWorkflow()
    {
        ArrayList<String> steps = new ArrayList<>();

        if (workflow != null)
        {
            steps.addAll(workflow);
        }

        if (workflow == null || !steps.contains("github"))
        {
            steps.add(0, "github");
        }

        return steps;
    }

    public static Repository load(File file) throws IOException
    {
        try (FileReader reader = new FileReader(file))
        {
            return transformer.deserialize(reader, Repository.class);
        }
    }

    private static boolean toSafeBoolean(Boolean bool, boolean dflt)
    {
        return bool == null ? dflt : bool.booleanValue();
    }

    @SuppressWarnings("unchecked")
    private static Collection<String> toSafeCollection(Object collection)
    {
        return (collection == null) ? Collections.emptySet()
                : Collections.unmodifiableCollection((Collection<String>) collection);
    }

    private static int toSafeInteger(Integer integer, int dflt)
    {
        return integer == null ? dflt : integer.intValue();
    }

    private static <T> Map<String, T> toSafeMap(Map<String, T> map)
    {
        return map == null ? Collections.emptyMap() : Collections.unmodifiableMap(map);
    }

    private static String toSafeString(String str, String dflt)
    {
        return str == null ? dflt : str;
    }

    public static class Access
    {
        private Collection<String> teams;

        private Collection<String> users;
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

    public static class Plugins
    {
        private final Map<String, String> plugins;

        public Plugins(Map<String, String> plugins)
        {
            this.plugins = plugins;
        }
    }

    public static class Settings
    {
        private static final Settings NULL = new Settings();

        private Branches branches;

        private Map<String, Permission> collaborators;

        private boolean hidden;

        private boolean initialize;

        private Map<String, Color> labels;

        private Map<String, Permission> teams;

        public boolean autoInitialize()
        {
            return initialize;
        }

        public Branches getBranches()
        {
            return branches == null ? Branches.NULL : branches;
        }

        public Map<String, Permission> getCollaborators()
        {
            return toSafeMap(collaborators);
        }

        public Map<String, Color> getLabels()
        {
            return toSafeMap(labels);
        }

        public Map<String, Permission> getTeams()
        {
            return toSafeMap(teams);
        }

        public boolean isPrivate()
        {
            return hidden;
        }

        public static class Branches
        {
            private static final Branches NULL = new Branches();

            private Collection<String> other;

            private String primary;

            private Map<String, Protection> protections;

            public String getDefault()
            {
                return toSafeString(primary, "master");
            }

            public Collection<String> getOther()
            {
                return toSafeCollection(other);
            }

            public Protection getProtection(String branch)
            {
                return toSafeMap(protections).getOrDefault(branch, Protection.NULL);
            }

            public static class Protection
            {
                private static final Protection NULL = new Protection();

                private Boolean includeAdministrators;

                private Access pushAccess;

                private RequiredChecks requiredStatusChecks;

                private RequireReviews requireReviews;

                private Boolean requireSignedCommits;

                public boolean enablePushAccess()
                {
                    return pushAccess != null;
                }

                public boolean getIncludeAdministrators()
                {
                    return toSafeBoolean(includeAdministrators, false);
                }

                public Collection<String> getPushTeams()
                {
                    return toSafeCollection(enablePushAccess() ? pushAccess.teams : null);
                }

                public Collection<String> getPushUsers()
                {
                    return toSafeCollection(enablePushAccess() ? pushAccess.users : null);
                }

                public RequiredChecks getRequiredChecks()
                {
                    return requiredStatusChecks == null ? RequiredChecks.NULL : requiredStatusChecks;
                }

                public RequireReviews getRequireReviews()
                {
                    return requireReviews == null ? RequireReviews.NULL : requireReviews;
                }

                public boolean getRequireSignedCommits()
                {
                    return toSafeBoolean(requireSignedCommits, false);
                }

                public boolean isEnabled()
                {
                    return this != NULL;
                }

                public static class RequiredChecks
                {
                    private static final RequiredChecks NULL = new RequiredChecks();

                    private Collection<String> contexts;

                    private Boolean requireUpToDate;

                    public Collection<String> getContexts()
                    {
                        return toSafeCollection(contexts);
                    }

                    public boolean getRequireUpToDate()
                    {
                        return toSafeBoolean(requireUpToDate, false);
                    }

                    public boolean isEnabled()
                    {
                        return this != NULL;
                    }
                }

                public static class RequireReviews
                {
                    private static final RequireReviews NULL = new RequireReviews();

                    private Boolean dismissStaleApprovals;

                    private Boolean requireCodeOwner;

                    private Integer requiredReviewers;

                    private Access restrictDismissals;

                    public boolean enableRestrictDismissals()
                    {
                        return restrictDismissals != null;
                    }

                    public Collection<String> getDismissalTeams()
                    {
                        return toSafeCollection(enableRestrictDismissals() ? restrictDismissals.teams : null);
                    }

                    public Collection<String> getDismissalUsers()
                    {
                        return toSafeCollection(enableRestrictDismissals() ? restrictDismissals.users : null);
                    }

                    public boolean getDismissStaleApprovals()
                    {
                        return toSafeBoolean(dismissStaleApprovals, false);
                    }

                    public boolean getRequireCodeOwner()
                    {
                        return toSafeBoolean(requireCodeOwner, false);
                    }

                    public int getRequiredReviewers()
                    {
                        return toSafeInteger(requiredReviewers, 1);
                    }

                    public boolean isEnabled()
                    {
                        return this != NULL;
                    }
                }
            }
        }
    }
}
