package io.dangernoodle.grt.internal;

import java.io.IOException;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;

import io.dangernoodle.grt.Arguments;
import io.dangernoodle.grt.Credentials;
import io.dangernoodle.grt.FileLoader;
import io.dangernoodle.grt.GithubClient;
import io.dangernoodle.grt.Workflow;
import io.dangernoodle.grt.cli.CommandLineParser;
import io.dangernoodle.grt.cli.CommandLineParser.Command;
import io.dangernoodle.grt.extensions.DefaultStatusCheckFactory;
import io.dangernoodle.grt.extensions.StatusCheckFactory;
import io.dangernoodle.grt.cli.RepositoryCommand;
import io.dangernoodle.grt.cli.ValidateCommand;
import io.dangernoodle.grt.json.JsonSchemaValidator;


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
    public Credentials getCredentials(Arguments arguments) throws IOException
    {
        return Credentials.load(new FileLoader(arguments.getRepoDir()).loadCredentials());
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
    public GithubWorkflow getGithubWorkflow(GithubClient client, StatusCheckFactory factory)
    {
        return new GithubWorkflow(client, factory);
    }

    @Produces
    @ApplicationScoped
    public JsonSchemaValidator getJsonSchemaValidator() throws IOException
    {
        return new EveritSchemaValidator(() -> getClass().getResourceAsStream(JsonSchemaValidator.SCHEMA));
    }

    @Produces
    @ApplicationScoped
    public RepositoryCommand getRepositoryCommand()
    {
        return new RepositoryCommand();
    }

    @Produces
    @ApplicationScoped
    public ValidateCommand.Executor getRepositoryExecutor(Arguments arguments, JsonSchemaValidator validator)
    {
        return new ValidateCommand.Executor(arguments, validator);
    }

    @Produces
    @ApplicationScoped
    public RepositoryCommand.Executor getRepositoryExecutor(Arguments arguments, WorkflowExecutor workflow)
    {
        return new RepositoryCommand.Executor(arguments, workflow);
    }

    @Produces
    @ApplicationScoped
    public StatusCheckFactory getStatusCheckFactory()
    {
        return new DefaultStatusCheckFactory();
    }

    @Produces
    @ApplicationScoped
    public ValidateCommand getValidateCommand()
    {
        return new ValidateCommand();
    }
}
