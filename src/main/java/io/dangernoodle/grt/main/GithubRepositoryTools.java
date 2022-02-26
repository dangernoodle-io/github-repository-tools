package io.dangernoodle.grt.main;

import com.google.inject.Injector;

import io.dangernoodle.grt.Arguments.ArgumentsBuilder;
import io.dangernoodle.grt.internal.Bootstrapper;
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
        Bootstrapper plugins = new Bootstrapper();

        Injector injector = plugins.getInjector();
        ArgumentsBuilder arguments = injector.getInstance(ArgumentsBuilder.class);

        CommandLine commandLine = new CommandLine(new GithubRepositoryTools(), createCommandFactory(injector));
        commandLine.setResourceBundle(plugins.getResourceBundle());

        plugins.getCommands()
               .forEach(commandLine::addSubcommand);

        commandLine.setExecutionStrategy(parseResult -> executionStrategy(parseResult, arguments))
                   .execute(args);
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
}
