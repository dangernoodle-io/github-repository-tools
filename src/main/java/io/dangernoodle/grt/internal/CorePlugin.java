package io.dangernoodle.grt.internal;

import static io.dangernoodle.grt.Constants.VALIDATE;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.multibindings.ProvidesIntoSet;

import org.kohsuke.github.connector.GitHubConnector;
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
import io.dangernoodle.grt.cli.exector.ValidatingExecutor;
import io.dangernoodle.grt.cli.exector.ValidationExecutor;
import io.dangernoodle.grt.client.GithubClient;
import io.dangernoodle.grt.client.GithubClientFactory;
import io.dangernoodle.grt.credentials.ChainedCredentials;
import io.dangernoodle.grt.credentials.EnvironmentCredentials;
import io.dangernoodle.grt.credentials.GithubCliCredentials;
import io.dangernoodle.grt.credentials.JsonCredentials;
import io.dangernoodle.grt.repository.RepositoryFactory;
import io.dangernoodle.grt.statuscheck.CommandStatusCheck;
import io.dangernoodle.grt.statuscheck.RepositoryStatusCheck;
import io.dangernoodle.grt.util.JsonTransformer;
import io.dangernoodle.grt.util.PathToXConverter;
import io.dangernoodle.grt.workflow.CommandWorkflow;
import io.dangernoodle.grt.workflow.LifecycleWorkflow;
import io.dangernoodle.grt.workflow.ValidationWorkflow;
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

            return new LifecycleWorkflow<>(command, converter, lifecycles);
        }

        @Provides
        @Singleton
        public Credentials credentials(Arguments arguments, JsonTransformer transformer, Set<Credentials> additional)
            throws IOException
        {
            List<Credentials> credentials = new ArrayList<>(additional);

            // command line credentials take precedence
            credentials.add(0, new GithubCliCredentials(arguments));

            // json credentials are checked last
            Path path = arguments.getCredentialsPath();
            if (Files.exists(path))
            {
                credentials.add(new JsonCredentials(transformer.deserialize(arguments.getCredentialsPath())));
            }

            return new ChainedCredentials(credentials);
        }

        @Provides
        public DefinitionExecutor definitionExecutor(Arguments arguments, Workflow<Path> workflow)
        {
            return new DefinitionExecutor(arguments.getDefinitionsRootPath(), workflow);
        }

        @Provides
        @Singleton
        public GithubClient githubClient(GithubClientFactory factory) throws IOException
        {
            return factory.create();
        }

        @Provides
        public GithubClientFactory githubClientFactory(Arguments arguments, Credentials credentials, GitHubConnector connector)
        {
            return new GithubClientFactory(credentials, connector, arguments.enabledForAll());
        }

        @Provides
        public GitHubConnector githubConnector(OkHttpClient okHttp)
        {
            return new OkHttpGitHubConnector(okHttp);
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
            return new RepositoryFactory(arguments.getConfigurationPath(), jsonTransformer);
        }

        @ProvidesIntoSet
        public Workflow<Repository> repositoryWorkflow(GithubClient client, StatusCheck statusCheck)
        {
            return DefaultWorkflows.repositoryWorkflow(client, statusCheck);
        }

        @Provides
        public StatusCheck statusCheck(Arguments arguments, Set<StatusCheck> statusChecks)
        {
            return new CommandStatusCheck(arguments.getCommand(), statusChecks);
        }

        @ProvidesIntoSet
        public Workflow<Repository> updateRefWorkflow(GithubClient client)
        {
            return DefaultWorkflows.updateRefWorkflow(client);
        }

        @Provides
        public ValidatingExecutor validatingExecutor(Injector injector)
        {
            return new ValidatingExecutor(injector);
        }

        @Provides
        public ValidationExecutor validatorExecutor(Arguments arguments, ValidationWorkflow workflow)
        {
            return new ValidationExecutor(arguments.getDefinitionsRootPath(), workflow);
        }

        @Provides
        public ValidationWorkflow validatorWorkflow(Arguments arguments, JsonTransformer transformer)
        {
            boolean detailed = VALIDATE.equals(arguments.getCommand());
            return new ValidationWorkflow(arguments.getConfigurationPath(), transformer, detailed);
        }

        @Override
        protected void configure()
        {
            Multibinder.newSetBinder(binder(), Credentials.class)
                       // this will create the default that is added
                       .addBinding().to(EnvironmentCredentials.Github.class);

            Multibinder.newSetBinder(binder(), StatusCheck.class)
                       // this will create the default that is added
                       .addBinding().to(RepositoryStatusCheck.class);

            Multibinder.newSetBinder(binder(), Workflow.class);
            Multibinder.newSetBinder(binder(), Workflow.Lifecycle.class);

            bind(Arguments.ArgumentsBuilder.class).in(Singleton.class);
            bind(Arguments.class).to(Arguments.ArgumentsBuilder.class);
        }
    }
}
