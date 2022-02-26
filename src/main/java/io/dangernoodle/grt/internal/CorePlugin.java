package io.dangernoodle.grt.internal;

import static io.dangernoodle.grt.Constants.VALIDATE;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.multibindings.ProvidesIntoSet;

import org.kohsuke.github.GitHubBuilder;
import org.kohsuke.github.extras.okhttp3.OkHttpConnector;

import io.dangernoodle.grt.Arguments;
import io.dangernoodle.grt.Command;
import io.dangernoodle.grt.Credentials;
import io.dangernoodle.grt.Plugin;
import io.dangernoodle.grt.Repository;
import io.dangernoodle.grt.StatusCheck;
import io.dangernoodle.grt.Workflow;
import io.dangernoodle.grt.cli.RepositoryCommand;
import io.dangernoodle.grt.cli.ValidateCommand;
import io.dangernoodle.grt.cli.exector.RepositoryExecutor;
import io.dangernoodle.grt.cli.exector.ValidateExecutor;
import io.dangernoodle.grt.credentials.ChainedCredentials;
import io.dangernoodle.grt.credentials.EnvironmentCredentials;
import io.dangernoodle.grt.credentials.JsonCredentials;
import io.dangernoodle.grt.repository.RepositoryFactory;
import io.dangernoodle.grt.statuscheck.RepositoryStatusCheck;
import io.dangernoodle.grt.util.GithubClient;
import io.dangernoodle.grt.util.JsonTransformer;
import io.dangernoodle.grt.util.JsonTransformer.JsonObject;
import io.dangernoodle.grt.workflow.CommandWorkflow;
import io.dangernoodle.grt.workflow.LifecycleWorkflow;
import io.dangernoodle.grt.workflow.PathToRepositoryWorkflow;
import io.dangernoodle.grt.workflow.StepWorkflow;
import io.dangernoodle.grt.workflow.ValidatorWorkflow;
import io.dangernoodle.grt.workflow.step.AddTeamsAndCollaborators;
import io.dangernoodle.grt.workflow.step.ClearWebhooks;
import io.dangernoodle.grt.workflow.step.CreateRepositoryBranches;
import io.dangernoodle.grt.workflow.step.CreateRepositoryLabels;
import io.dangernoodle.grt.workflow.step.EnableBranchProtections;
import io.dangernoodle.grt.workflow.step.FindOrCreateRepository;
import io.dangernoodle.grt.workflow.step.SetRepositoryOptions;
import okhttp3.OkHttpClient;


public class CorePlugin implements Plugin
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

    @Override
    public Optional<String> getResourceBundle()
    {
        return Optional.of("GithubRepositoryTools");
    }

    private class CoreModule extends AbstractModule
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
        @Singleton
        public Arguments arguments()
        {
            return new Arguments.ArgumentsBuilder();
        }
        
        @Provides
        public StatusCheck getStatusCheckFactory()
        {
            return new RepositoryStatusCheck();
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

        @Provides
        public RepositoryFactory repositoryFactory(Arguments arguments, JsonTransformer jsonTransformer) throws IOException
        {
            return new RepositoryFactory(arguments.getConfiguration(), jsonTransformer);
        }

        @ProvidesIntoSet
        public Workflow<Repository> repositoryWorkflow(GithubClient client, StatusCheck factory)
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

        @Override
        protected void configure()
        {
            Multibinder.newSetBinder(binder(), Workflow.class);
            Multibinder.newSetBinder(binder(), Workflow.Lifecycle.class);
        }

        @SafeVarargs
        private <T> StepWorkflow<T> createStepWorkflow(String name, Workflow.Step<T>... steps)
        {
            return new StepWorkflow<>(name, List.of(steps));
        }
    }
}
