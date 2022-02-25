package io.dangernoodle.grt.internal;

import static io.dangernoodle.grt.Constants.VALIDATE;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.multibindings.OptionalBinder;
import com.google.inject.multibindings.ProvidesIntoSet;

import org.kohsuke.github.GitHubBuilder;
import org.kohsuke.github.extras.okhttp3.OkHttpConnector;

import io.dangernoodle.grt.Arguments;
import io.dangernoodle.grt.Command;
import io.dangernoodle.grt.Credentials;
import io.dangernoodle.grt.GithubClient;
import io.dangernoodle.grt.Repository;
import io.dangernoodle.grt.Workflow;
import io.dangernoodle.grt.cli.RepositoryCommand;
import io.dangernoodle.grt.cli.ValidateCommand;
import io.dangernoodle.grt.cli.executor.RepositoryExecutor;
import io.dangernoodle.grt.cli.executor.ValidateExecutor;
import io.dangernoodle.grt.creds.ChainedCredentials;
import io.dangernoodle.grt.creds.EnvironmentCredentials;
import io.dangernoodle.grt.creds.JsonCredentials;
import io.dangernoodle.grt.ext.statuschecks.RepositoryStatusCheckProvider;
import io.dangernoodle.grt.ext.statuschecks.StatusCheckProvider;
import io.dangernoodle.grt.steps.AddTeamsAndCollaborators;
import io.dangernoodle.grt.steps.ClearWebhooks;
import io.dangernoodle.grt.steps.CreateRepositoryBranches;
import io.dangernoodle.grt.steps.CreateRepositoryLabels;
import io.dangernoodle.grt.steps.EnableBranchProtections;
import io.dangernoodle.grt.steps.FindOrCreateRepository;
import io.dangernoodle.grt.steps.SetRepositoryOptions;
import io.dangernoodle.grt.utils.JsonTransformer;
import io.dangernoodle.grt.utils.JsonTransformer.JsonObject;
import io.dangernoodle.grt.utils.RepositoryFactory;
import io.dangernoodle.grt.workflows.CommandWorkflow;
import io.dangernoodle.grt.workflows.LifecycleWorkflow;
import io.dangernoodle.grt.workflows.PathToRepositoryWorkflow;
import io.dangernoodle.grt.workflows.StepWorkflow;
import io.dangernoodle.grt.workflows.ValidatorWorkflow;
import okhttp3.OkHttpClient;


public class CoreModule extends AbstractModule
{
    @Provides
    public CommandWorkflow commandWorkflow(Arguments arguments, Set<Workflow<Repository>> workflows)
    {
        // only workflow 'provides' methods annotated with '@ProvidesIntoSet' will appear here
        return new CommandWorkflow(arguments.getCommand(), arguments.ignoreErrors(), workflows);
    }

    @Provides
    @Singleton
    public Credentials credentials(Arguments arguments, JsonTransformer jsonTransformer) throws IOException
    {
        JsonObject json = jsonTransformer.deserialize(arguments.getCredentials());

        return new ChainedCredentials(
                new JsonCredentials(json),
                new EnvironmentCredentials());
    }

    @Provides
    public GithubClient getGithubClient(Credentials credentials, OkHttpClient okHttp) throws IOException
    {
        GitHubBuilder builder = new GitHubBuilder();
        builder.withOAuthToken(credentials.getGithubToken())
               .withConnector(new OkHttpConnector(okHttp));

        return GithubClient.createClient(builder.build());
    }

    @Provides
    public StatusCheckProvider getStatusCheckFactory()
    {
        return new RepositoryStatusCheckProvider();
    }

    @Provides
    @Singleton
    public JsonTransformer jsonTransformer()
    {
        return new JsonTransformer();
    }

    @Provides
    @Singleton
    public OkHttpClient okHttpClient()
    {
        return new OkHttpClient();
    }

    @Provides
    public RepositoryFactory repositoryFactory(Arguments arguments, JsonTransformer jsonTransformer) throws IOException
    {
        return new RepositoryFactory(arguments.getConfiguration(), jsonTransformer);
    }

    @Provides
    public RepositoryExecutor repositoryExecutor(Arguments arguments, RepositoryFactory factory, Set<Workflow<Repository>> workflows,
            Set<Workflow.Lifecycle> lifecycles)
    {
        String command = arguments.getCommand();

        // only workflow 'provides' methods annotated with '@ProvidesIntoSet' will appear here
        CommandWorkflow delegate = new CommandWorkflow(command, arguments.ignoreErrors(), workflows);

        PathToRepositoryWorkflow converter = new PathToRepositoryWorkflow(factory, delegate);
        LifecycleWorkflow<Path> workflow = new LifecycleWorkflow<>(converter, Collections.emptySet(), command);

        return new RepositoryExecutor(arguments.getDefinitionsRoot(), workflow);
    }

    @Override
    protected void configure()
    {
        Multibinder.newSetBinder(binder(), Workflow.class);
        Multibinder.newSetBinder(binder(), Workflow.Lifecycle.class);
    }

    @ProvidesIntoSet
    public Workflow<Repository> repositoryWorkflow(GithubClient client, StatusCheckProvider factory)
    {
        return createStepWorkflow("repository",
                new FindOrCreateRepository(client),
                new SetRepositoryOptions(client),
                new CreateRepositoryLabels(client),
                new AddTeamsAndCollaborators(client),
                new CreateRepositoryBranches(client),
                new EnableBranchProtections(client, factory),
                // optional step enabled by a command line argument
                new ClearWebhooks(client));
    }

    @Provides
    public ValidateExecutor validateExecutor(Arguments arguments, ValidatorWorkflow workflow)
    {
        return new ValidateExecutor(arguments.getDefinitionsRoot(), workflow);
    }

    @Provides
    public ValidatorWorkflow validatorWorkflow(Arguments arguments, JsonTransformer transformer)
    {
        boolean detailed = VALIDATE.equals(arguments.getCommand());
        return new ValidatorWorkflow(arguments.getConfiguration(), transformer, detailed);
    }

    @SafeVarargs
    private <T> StepWorkflow<T> createStepWorkflow(String name, Workflow.Step<T>... steps)
    {
        return new StepWorkflow<>(name, List.of(steps));
    }

    public static class Plugin implements io.dangernoodle.grt.Plugin
    {
        @Override
        public Collection<Class<? extends Command>> getCommands()
        {
            return List.of(
                    RepositoryCommand.class,
                    ValidateCommand.class);
        }

        @Override
        public Collection<Module> getModules()
        {
            return List.of(new CoreModule());
        }
    }
}
