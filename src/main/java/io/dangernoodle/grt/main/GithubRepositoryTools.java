package io.dangernoodle.grt.main;

import java.nio.file.Path;
import java.util.ResourceBundle;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import io.dangernoodle.grt.Arguments;
import io.dangernoodle.grt.internal.CoreModule;
import io.dangernoodle.grt.internal.PluginsManager;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.IFactory;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParseResult;


@Command(name = "grt")
public class GithubRepositoryTools
{
    @Option(names = "--rootDir", required = true)
    private String rootDir;

    public static void main(String... args) throws Exception
    {
        PluginsManager plugins = new PluginsManager(new CoreModule.Plugin());

        Injector injector = plugins.getInjector(createArgumentsModule());
        ArgumentsBuilder arguments = (ArgumentsBuilder) injector.getInstance(Arguments.class);

        CommandLine commandLine = new CommandLine(new GithubRepositoryTools(), createCommandFactory(injector));

        //commandLine.setResourceBundle(ResourceBundle.);
        
        plugins.getCommands()
               .forEach(commandLine::addSubcommand);

        commandLine.setExecutionStrategy(parseResult -> executionStrategy(parseResult, arguments))
                   .execute(args);
    }

    private static AbstractModule createArgumentsModule()
    {
        return new AbstractModule()
        {
            @Provides
            @Singleton
            public Arguments arguments()
            {
                return new ArgumentsBuilder();
            }
        };
    }

    private static IFactory createCommandFactory(Injector injector)
    {
        /*
         * this factory is only used by picocli to create commands and other object it needs, so it's safe to fall back
         * to the default factory
         */
        return new IFactory()
        {
            @Override
            public <K> K create(Class<K> cls) throws Exception
            {
                try
                {
                    return cls.getConstructor(Injector.class)
                              .newInstance(injector);
                }
                catch (@SuppressWarnings("unused") NoSuchMethodException e)
                {
                    return CommandLine.defaultFactory().create(cls);
                }
            }
        };
    }

    private static int executionStrategy(ParseResult parseResult, ArgumentsBuilder arguments)
    {
        if (parseResult.errors().isEmpty())
        {
            arguments.initialize(parseResult);
        }

        return new CommandLine.RunLast().execute(parseResult);
    }

    private static class ArgumentsBuilder implements Arguments
    {
        private String command;

        private String rootDir;

        private boolean ignoreErrors;

        @Override
        public String getCommand()
        {
            return command;
        }

        @Override
        public Path getRoot()
        {
            return Path.of(rootDir);
        }

        @Override
        public boolean ignoreErrors()
        {
            return ignoreErrors;
        }

        void initialize(ParseResult parseResult)
        {
            this.rootDir = parseResult.matchedOption(Arguments.ROOT_DIR)
                                      .getValue();

            if (parseResult.hasSubcommand())
            {
                initFromCommand(parseResult.subcommand());
            }
        }

        private void initFromCommand(ParseResult parseResult)
        {
            if (!parseResult.errors().isEmpty())
            {
                return;
            }

            this.command = parseResult.commandSpec().name();
            this.ignoreErrors = parseResult.matchedOptionValue(Arguments.IGNORE_ERRORS, false);
        }
    }
}
