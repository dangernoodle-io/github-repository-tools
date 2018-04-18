package io.dangernoodle.grt;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

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

    public GHRepository createRepository(String name, Settings settings) throws IOException
    {
        return createRepository(name, settings, github.createRepository(name));
    }

    public GHRepository createRepository(String name, String organization, Settings settings) throws IOException
    {
        return createRepository(name, settings, getOrganization(organization).createRepository(name));
    }

    public GHMyself getMyself() throws IOException
    {
        return (GHMyself) computeIfAbsent(collaborators, GHMyself.class.toString(), l -> {
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

    private GHRepository createRepository(String name, Settings settings, GHCreateRepositoryBuilder builder) throws IOException
    {
        return computeIfAbsent(repositories, name, n -> {
            return builder.autoInit(settings.autoInitialize())
                          .private_(settings.isPrivate())
                          .create();
        });
    }

    public static GithubClient createClient(String token) throws IOException
    {
        GithubClient client = new GithubClient(GitHub.connectUsingOAuth(token));
        client.github.checkApiUrlValidity();

        return client;
    }

    @ApplicationScoped
    public static class GithubClientProducer
    {
        @Produces
        @Singleton
        public GithubClient get(Credentials credentials) throws IOException
        {
            return GithubClient.createClient(credentials.getGithubToken());
        }
    }

    @FunctionalInterface
    private interface IOExceptionFunction<T, R>
    {
        R apply(T t) throws IOException;
    }
}
