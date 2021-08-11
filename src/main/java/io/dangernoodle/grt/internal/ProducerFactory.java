package io.dangernoodle.grt.internal;

import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;

import org.kohsuke.github.GitHubBuilder;
import org.kohsuke.github.extras.okhttp3.OkHttpConnector;

import io.dangernoodle.grt.Arguments;
import io.dangernoodle.grt.Credentials;
import io.dangernoodle.grt.GithubClient;
import io.dangernoodle.grt.Workflow;
import io.dangernoodle.grt.cli.CommandLineParser;
import io.dangernoodle.grt.cli.CommandLineParser.Command;
import io.dangernoodle.grt.cli.RepositoryCommand;
import io.dangernoodle.grt.cli.ValidateCommand;
import io.dangernoodle.grt.ext.statuschecks.RepositoryStatusCheckProvider;
import io.dangernoodle.grt.ext.statuschecks.StatusCheckProvider;
import io.dangernoodle.grt.utils.CredentialsChain;
import io.dangernoodle.grt.utils.EnvironmentCredentials;
import io.dangernoodle.grt.utils.JsonFileCredentials;
import io.dangernoodle.grt.utils.JsonTransformer;
import io.dangernoodle.grt.utils.RepositoryMerger;
import okhttp3.OkHttpClient;


@ApplicationScoped
public class ProducerFactory
{
    private static final Arguments arguments = new Arguments();
    private static final OkHttpClient okHttp = new OkHttpClient();

    @Produces
    public Arguments getArguments()
    {
        // @ApplicationScoped causes the bean to be proxied, which is no beuno for JCommander
        return arguments;
    }

    @Produces
    @ApplicationScoped
    public CommandLineParser getCommandLineDelegate(Instance<Command> instance, Arguments arguments)
    {
        return new CommandLineParser(toCollection(instance), arguments);
    }

    @Produces
    @ApplicationScoped
    @SuppressWarnings("unchecked")
    public Credentials getCredentials(FileLoader fileLoader, JsonTransformer transformer)
    {
        return new CredentialsChain(
                JsonFileCredentials.loadCredentials(fileLoader, transformer),
                new EnvironmentCredentials());
    }

    @Produces
    @ApplicationScoped
    public WorkflowExecutor getExecutor(Instance<Workflow> workflows, Instance<Workflow.PrePost> prePost)
    {
        return new WorkflowExecutor(toCollection(workflows), toCollection(prePost));
    }

    @Produces
    @ApplicationScoped
    public GithubClient getGithubClient(Credentials credentials, OkHttpClient okHttp) throws IOException
    {
        GitHubBuilder builder = new GitHubBuilder();
        builder.withOAuthToken(credentials.getGithubToken())
               .withConnector(new OkHttpConnector(okHttp));

        return GithubClient.createClient(builder.build());
    }

    @Produces
    @ApplicationScoped
    public GithubWorkflow getGithubWorkflow(GithubClient client, StatusCheckProvider factory)
    {
        return new GithubWorkflow(client, factory);
    }

    @Produces
    @ApplicationScoped
    public JsonTransformer getJsonTransformer()
    {
        return new JsonTransformer();
    }

    @Produces
    public OkHttpClient getOkHttpClient()
    {
        // @ApplicationScoped causes the bean to be proxied, and okhttp has some final methods
        return okHttp;
    }

    @Produces
    @ApplicationScoped
    public RepositoryCommand getRepositoryCommand()
    {
        return new RepositoryCommand();
    }

    @Produces
    @ApplicationScoped
    public FileLoader getFileLoader(Arguments arguments)
    {
        return new FileLoader(arguments.getRepoDir());
    }

    @Produces
    @ApplicationScoped
    public RepositoryCommand.Executor getRepositoryExecutor(FileLoader fileLoader, WorkflowExecutor workflowExecutor,
            RepositoryMerger repositoryMerger)
    {
        return new RepositoryCommand.Executor(workflowExecutor, repositoryMerger, fileLoader);
    }

    @Produces
    @ApplicationScoped
    public StatusCheckProvider getStatusCheckFactory()
    {
        return new RepositoryStatusCheckProvider();
    }

    @Produces
    @ApplicationScoped
    public RepositoryMerger getRepositoryMerger(JsonTransformer transformer)
    {
        return new RepositoryMerger(transformer);
    }

    @Produces
    @ApplicationScoped
    public ValidateCommand getValidateCommand()
    {
        return new ValidateCommand();
    }

    @Produces
    @ApplicationScoped
    public ValidateCommand.Executor getValidateExecutor(FileLoader fileLoader, JsonTransformer transformer)
    {
        return new ValidateCommand.Executor(fileLoader, transformer);
    }

    private <T> Collection<T> toCollection(Instance<T> instance)
    {
        return instance.stream().collect(Collectors.toList());
    }
}
