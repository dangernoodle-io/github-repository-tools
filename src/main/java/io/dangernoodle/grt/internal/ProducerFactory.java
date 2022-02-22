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
import io.dangernoodle.grt.creds.CredentialsChain;
import io.dangernoodle.grt.creds.EnvironmentCredentials;
import io.dangernoodle.grt.creds.JsonFileCredentials;
import io.dangernoodle.grt.ext.statuschecks.RepositoryStatusCheckProvider;
import io.dangernoodle.grt.ext.statuschecks.StatusCheckProvider;
import io.dangernoodle.grt.utils.JsonTransformer;
import io.dangernoodle.grt.utils.RepositoryFactory;
import io.dangernoodle.grt.utils.RepositoryMerger;
import io.dangernoodle.grt.workflows.CommandWorkflow;
import okhttp3.OkHttpClient;


@ApplicationScoped
public class ProducerFactory
{
    private static final Arguments arguments = new Arguments();
    private static final OkHttpClient okHttp = new OkHttpClient();

    private static final GithubWorkflowsFactory workflowFactory = new GithubWorkflowsFactory();
    
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
    public FileLoader getFileLoader(Arguments arguments)
    {
        return new FileLoader(arguments.getRepoDir().toFile().toString());
    }

    @Produces
    @ApplicationScoped
    public RepositoryFactory getRepositoryFactory(Arguments arguments, JsonTransformer transformer) throws IOException
    {
        return new RepositoryFactory(transformer, arguments.getRepoDir());
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
    public Workflow getRepositoryWorkflow(GithubClient client, StatusCheckProvider factory)
    {
        return workflowFactory.repositoryWorkflow(client, factory);
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
    public RepositoryCommand.Executor getRepositoryExecutor(RepositoryFactory factory, Arguments arguments, Instance<Workflow> workflows)
    {
        return new RepositoryCommand.Executor(factory,
                new CommandWorkflow(arguments.getCommand(), toCollection(workflows), arguments.isIgnoreErrors()));
    }

    @Produces
    @ApplicationScoped
    public RepositoryMerger getRepositoryMerger(JsonTransformer transformer)
    {
        return new RepositoryMerger(transformer);
    }

    @Produces
    @ApplicationScoped
    public StatusCheckProvider getStatusCheckFactory()
    {
        return new RepositoryStatusCheckProvider();
    }

//    @Produces
//    @ApplicationScoped
//    public UpdateRefCommand getUpdatRefCommand()
//    {
//        return new UpdateRefCommand();
//    }

    @Produces
    @ApplicationScoped
    public ValidateCommand getValidateCommand()
    {
        return new ValidateCommand();
    }

    @Produces
    @ApplicationScoped
    public ValidateCommand.ValidatorExecutor getValidateExecutor(Arguments arguments, JsonTransformer transformer)
    {
        return new ValidateCommand.ValidatorExecutor(transformer, arguments.getRepoDir());
    }

    private <T> Collection<T> toCollection(Instance<T> instance)
    {
        return instance.stream().collect(Collectors.toList());
    }
}
