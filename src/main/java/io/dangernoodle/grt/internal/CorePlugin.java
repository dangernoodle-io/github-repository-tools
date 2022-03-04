package io.dangernoodle.grt.internal;

import static io.dangernoodle.grt.Constants.REPOSITORY;
import static io.dangernoodle.grt.Constants.UPDATE_REF;
import static io.dangernoodle.grt.Constants.VALIDATE;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
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
import org.kohsuke.github.extras.okhttp3.OkHttpGitHubConnector;

import io.dangernoodle.grt.Arguments;
import io.dangernoodle.grt.Command;
import io.dangernoodle.grt.Credentials;
import io.dangernoodle.grt.Plugin;
import io.dangernoodle.grt.Repository;
import io.dangernoodle.grt.StatusCheck;
import io.dangernoodle.grt.Workflow;
import io.dangernoodle.grt.cli.RepositoryCommand;
import io.dangernoodle.grt.cli.UpdateRefCommand;
import io.dangernoodle.grt.cli.ValidateCommand;
import io.dangernoodle.grt.cli.exector.DefinitionExecutor;
import io.dangernoodle.grt.credentials.ChainedCredentials;
import io.dangernoodle.grt.credentials.EnvironmentCredentials;
import io.dangernoodle.grt.credentials.JsonCredentials;
import io.dangernoodle.grt.repository.RepositoryFactory;
import io.dangernoodle.grt.statuscheck.CommandStatusCheck;
import io.dangernoodle.grt.statuscheck.RepositoryStatusCheck;
import io.dangernoodle.grt.util.GithubClient;
import io.dangernoodle.grt.util.JsonTransformer;
import io.dangernoodle.grt.util.PathToXConverter;
import io.dangernoodle.grt.workflow.CommandWorkflow;
import io.dangernoodle.grt.workflow.LifecycleWorkflow;
import io.dangernoodle.grt.workflow.StepWorkflow;
import io.dangernoodle.grt.workflow.ValidatorWorkflow;
import io.dangernoodle.grt.workflow.step.AddTeamsAndCollaborators;
import io.dangernoodle.grt.workflow.step.ClearWebhooks;
import io.dangernoodle.grt.workflow.step.CreateOrUpdateReference;
import io.dangernoodle.grt.workflow.step.CreateRepositoryBranches;
import io.dangernoodle.grt.workflow.step.CreateRepositoryLabels;
import io.dangernoodle.grt.workflow.step.EnableBranchProtections;
import io.dangernoodle.grt.workflow.step.FindCommitBy;
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
                UpdateRefCommand.class,
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
        public Workflow<Path> commandWorkflow(Arguments arguments, RepositoryFactory factory, Set<Workflow<Repository>> workflows,
                Set<Workflow.Lifecycle> lifecycles)
        {
            String command = arguments.getCommand();

            // only workflow 'provides' methods annotated with '@ProvidesIntoSet' will appear here
            CommandWorkflow delegate = new CommandWorkflow(command, arguments.ignoreErrors(), workflows);

            PathToXConverter<Repository> converter = new PathToXConverter<>(delegate, path -> factory.load(path));
            LifecycleWorkflow<Path> workflow = new LifecycleWorkflow<>(converter, lifecycles, command);

            return workflow;
        }

        @Provides
        public CommandWorkflow commandWorkflow(Arguments arguments, Set<Workflow<Repository>> workflows)
        {
            // only workflow 'provides' methods annotated with '@ProvidesIntoSet' will appear here
            return new CommandWorkflow(arguments.getCommand(), arguments.ignoreErrors(), workflows);
        }

        @Provides
        @Singleton
        public Credentials credentials(Arguments arguments, Set<Credentials> credentials, JsonTransformer jsonTransformer)
            throws IOException
        {
            Credentials json = new JsonCredentials(jsonTransformer.deserialize(arguments.getCredentials()));
            return new ChainedCredentials(toList(json, credentials));
        }

        @Provides
        public DefinitionExecutor definitionExecutor(Arguments arguments, Workflow<Path> workflow)
        {
            return new DefinitionExecutor(arguments.getDefinitionsRoot(), workflow);
        }

        @Provides
        public GithubClient githubClient(Credentials credentials, OkHttpClient okHttp) throws IOException
        {
            GitHubBuilder builder = new GitHubBuilder();
            builder.withOAuthToken(credentials.getGithubToken())
                   .withConnector(new OkHttpGitHubConnector(okHttp));                   

            return GithubClient.createClient(builder);
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

        @ProvidesIntoSet
        public Workflow<Repository> repositoryWorkflow(GithubClient client, StatusCheck statusCheck)
        {
            return createStepWorkflow(REPOSITORY,
                    new FindOrCreateRepository(client),
                    new SetRepositoryOptions(client),
                    new CreateRepositoryLabels(client),
                    new AddTeamsAndCollaborators(client),
                    new CreateRepositoryBranches(client),
                    new EnableBranchProtections(client, statusCheck),
                    // optional step enabled by a command line argument
                    new ClearWebhooks(client));
        }

        @Provides
        public StatusCheck statusCheck(Arguments arguments, Set<StatusCheck> statusChecks)
        {
            return new CommandStatusCheck(arguments.getCommand(), statusChecks);
        }

        @ProvidesIntoSet
        public Workflow<Repository> updateRefWorkflow(GithubClient client)
        {
            return createStepWorkflow(UPDATE_REF,
                    new FindOrCreateRepository(client, false),
                    new FindCommitBy.Tag(client),
                    new FindCommitBy.Sha1(client),
                    new CreateOrUpdateReference(client));
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
            Multibinder.newSetBinder(binder(), Credentials.class)
                       // this will create the default that is added
                       .addBinding().to(EnvironmentCredentials.class);

            Multibinder.newSetBinder(binder(), StatusCheck.class)
                       // this will create the default that is added
                       .addBinding().to(RepositoryStatusCheck.class);

            Multibinder.newSetBinder(binder(), Workflow.class);
            Multibinder.newSetBinder(binder(), Workflow.Lifecycle.class);

            bind(Arguments.ArgumentsBuilder.class).in(Singleton.class);
            bind(Arguments.class).to(Arguments.ArgumentsBuilder.class);
        }

        @SafeVarargs
        private <T> StepWorkflow<T> createStepWorkflow(String name, Workflow.Step<T>... steps)
        {
            return new StepWorkflow<>(name, List.of(steps));
        }
       
        private <T> List<T> toList(T first, Set<T> others)
        {
            List<T> list = new ArrayList<>(others);
            list.add(0, first);

            return list;
        }
    }
}
