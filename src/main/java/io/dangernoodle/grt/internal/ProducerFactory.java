package io.dangernoodle.grt.internal;

import java.io.IOException;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;

import io.dangernoodle.grt.Arguments;
import io.dangernoodle.grt.Credentials;
import io.dangernoodle.grt.GithubClient;
import io.dangernoodle.grt.Workflow;
import io.dangernoodle.grt.cli.CommandLineParser;
import io.dangernoodle.grt.cli.CommandLineParser.Command;
import io.dangernoodle.grt.ext.statuschecks.RepositoryStatusCheckProvider;
import io.dangernoodle.grt.ext.statuschecks.StatusCheckProvider;
import io.dangernoodle.grt.cli.RepositoryCommand;
import io.dangernoodle.grt.cli.ValidateCommand;
import io.dangernoodle.grt.utils.JsonTransformer;


@ApplicationScoped
public class ProducerFactory
{
    private static final Arguments arguments = new Arguments();

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
        return new CommandLineParser(instance.stream().collect(Collectors.toList()), arguments);
    }

    @Produces
    @ApplicationScoped
    @SuppressWarnings("unchecked")
    public Credentials getCredentials(Arguments arguments, JsonTransformer transformer) throws IOException
    {
        return new Credentials(transformer.deserialize(new FileLoader(arguments.getRepoDir()).loadCredentials()));
    }

    @Produces
    @ApplicationScoped
    public WorkflowExecutor getExecutor(Instance<Workflow> instance)
    {
        return new WorkflowExecutor(instance.stream().collect(Collectors.toList()));
    }

    @Produces
    @ApplicationScoped
    public GithubClient getGithubClient(Credentials credentials) throws IOException
    {
        return GithubClient.createClient(credentials.getGithubToken());
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
    @ApplicationScoped
    public RepositoryCommand getRepositoryCommand()
    {
        return new RepositoryCommand();
    }

    @Produces
    @ApplicationScoped
    public RepositoryCommand.Executor getRepositoryExecutor(Arguments arguments, WorkflowExecutor workflow,
            JsonTransformer transformer)
    {
        return new RepositoryCommand.Executor(arguments, workflow, transformer);
    }

    @Produces
    @ApplicationScoped
    public StatusCheckProvider getStatusCheckFactory()
    {
        return new RepositoryStatusCheckProvider();
    }

    @Produces
    @ApplicationScoped
    public ValidateCommand getValidateCommand()
    {
        return new ValidateCommand();
    }

    @Produces
    @ApplicationScoped
    public ValidateCommand.Executor getValidateExecutor(Arguments arguments, JsonTransformer transformer)
    {
        return new ValidateCommand.Executor(arguments, transformer);
    }
}
