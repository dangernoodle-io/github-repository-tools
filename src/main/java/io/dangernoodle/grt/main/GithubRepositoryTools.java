package io.dangernoodle.grt.main;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.LogManager;
import java.util.stream.Collectors;

import com.google.inject.Injector;

import org.slf4j.bridge.SLF4JBridgeHandler;

import io.dangernoodle.grt.Arguments.ArgumentsBuilder;
import io.dangernoodle.grt.internal.Bootstrapper;
import picocli.CommandLine;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Help;
import picocli.CommandLine.IFactory;
import picocli.CommandLine.IHelpFactory;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParseResult;


@Command(name = "grt")
public class GithubRepositoryTools
{
    static
    {
        LogManager.getLogManager().reset();
        SLF4JBridgeHandler.install();
    }

    @ArgGroup(exclusive = false)
    private BotApp botApp;

    @Option(names = "--rootDir", required = true)
    private Path rootDir;

    public static void main(String... args) throws Exception
    {
        Bootstrapper plugins = new Bootstrapper();

        Injector injector = plugins.getInjector();
        ArgumentsBuilder arguments = injector.getInstance(ArgumentsBuilder.class);

        CommandLine commandLine = new CommandLine(new GithubRepositoryTools(), createCommandFactory(injector));
        commandLine.setHelpFactory(createHelpFactory())
                   .setResourceBundle(plugins.getResourceBundle());

        plugins.getCommands()
               .forEach(commandLine::addSubcommand);

        commandLine.setExecutionStrategy(parseResult -> executionStrategy(parseResult, arguments))
                   .execute(args);
    }

    private static IFactory createCommandFactory(Injector injector)
    {
        // only used by picocli to create commands and other object it needs, so it's safe to fall back to the default
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

    private static IHelpFactory createHelpFactory()
    {
        // sort 'subcommands' by name, not the order they get added
        return (commandSpec, colorScheme) -> new Help(commandSpec, colorScheme)
        {
            @Override
            public Map<String, Help> subcommands()
            {
                return super.subcommands().entrySet()
                                          .stream()
                                          .sorted(Map.Entry.comparingByKey())
                                          .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                                  (oldValue, newValue) -> oldValue, LinkedHashMap::new));
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

    private static class BotApp
    {
        @Option(names = "--appId", required = true)
        private String appId;

        @ArgGroup(exclusive = true)
        private Auth auth;

        @Option(names = "--installId", required = true)
        private String installId;

        private static class Auth
        {
            @Option(names = "--appKey")
            private Path privateKey;

            @Option(names = "--appToken")
            private String token;
        }
    }
}
