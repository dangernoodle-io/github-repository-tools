package io.dangernoodle.grt;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import org.kohsuke.github.GHCreateRepositoryBuilder;
import org.kohsuke.github.GHEventPayload;
import org.kohsuke.github.GHMyself;
import org.kohsuke.github.GHOrganization;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHTeam;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;

import io.dangernoodle.grt.Repository.Settings;


public class GithubClient
{
    public final GitHub github;

    private final Map<String, GHUser> collaborators;

    private final Map<String, GHOrganization> organizations;

    private final Map<GHOrganization, Map<String, GHTeam>> orgTeams;

    private final Map<String, GHRepository> repositories;

    public GithubClient(GitHub github)
    {
        this.github = github;

        this.collaborators = createMap();
        this.repositories = createMap();
        this.organizations = createMap();
        this.orgTeams = createMap();
    }

    public GHRepository createOrgRepository(Repository repository) throws IOException
    {
        GHOrganization ghOrg = getOrganization(repository.getOrganization());
        return createRepository(repository, name -> {
            try
            {
                return ghOrg.createRepository(name);
            }
            catch (IOException e)
            {
                // TODO: remove if https://github.com/kohsuke/github-api/pull/436 merged
                throw new UncheckedIOException(e);
            }
        });
    }

    public GHRepository createUserRepository(Repository repository) throws IOException
    {
        return createRepository(repository, name -> github.createRepository(name));
    }

    public String getCurrentLogin() throws IOException
    {
        return github.getMyself().getLogin();
    }

    public GHMyself getMyself() throws IOException
    {
        return (GHMyself) computeIfAbsent(collaborators, GHMyself.class.getName(), n -> {
            return github.getMyself();
        });
    }

    public GHOrganization getOrganization(String name) throws IOException
    {
        return computeIfAbsent(organizations, name, n -> github.getOrganization(name));
    }

    public GHRepository getRepository(String repository) throws IOException
    {
        return computeIfAbsent(repositories, repository, name -> getMyself().getRepository(name));
    }

    public GHRepository getRepository(String organization, String repository) throws IOException
    {
        return computeIfAbsent(repositories, repository, name -> getOrganization(organization).getRepository(name));
    }

    public GHTeam getTeam(String organization, String slug) throws IOException
    {
        GHOrganization ghOrganization = getOrganization(organization);
        Map<String, GHTeam> teams = computeIfAbsent(orgTeams, ghOrganization, o -> createMap());

        return computeIfAbsent(teams, slug, s -> {
            return ghOrganization.getTeamBySlug(s);
        });
    }

    public GHUser getUser(String login) throws IOException
    {
        return computeIfAbsent(collaborators, login, l -> {
            return github.getUser(l);
        });
    }

    public GHEventPayload.Push parsePushEvent(Reader reader) throws IOException
    {
        return github.parseEventPayload(reader, GHEventPayload.Push.class);
    }

    private <K, V> V computeIfAbsent(Map<K, V> map, K name, IOExceptionFunction<K, V> function) throws IOException
    {
        try
        {
            return map.computeIfAbsent(name, n -> {
                try
                {
                    return function.apply(name);
                }
                catch (IOException e)
                {
                    throw new UncheckedIOException(e);
                }
            });
        }
        catch (UncheckedIOException e)
        {
            throw e.getCause();
        }
    }

    private <K, V> Map<K, V> createMap()
    {
        return new ConcurrentHashMap<>(8, 0.9f);
    }

    private GHRepository createRepository(Repository repository, Function<String, GHCreateRepositoryBuilder> function)
        throws IOException
    {
        String name = repository.getName();
        Settings settings = repository.getSettings();

        GHCreateRepositoryBuilder builder = function.apply(name);

        return computeIfAbsent(repositories, name, n -> {
            // would be nice to chain, but then the all these would have to be mocked to return the builder
            builder.autoInit(settings.autoInitialize());
            builder.private_(settings.isPrivate());
            builder.description(repository.getDescription());
            builder.homepage(repository.getHomepage());

            return builder.create();
        });
    }

    public static GithubClient createClient(String token) throws IOException
    {
        GithubClient client = new GithubClient(GitHub.connectUsingOAuth(token));
        client.github.checkApiUrlValidity();

        return client;
    }

    @FunctionalInterface
    private interface IOExceptionFunction<T, R>
    {
        R apply(T t) throws IOException;
    }
}
