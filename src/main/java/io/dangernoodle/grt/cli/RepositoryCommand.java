package io.dangernoodle.grt.cli;

import java.util.Map;

import com.google.inject.Injector;

import io.dangernoodle.grt.cli.exector.RepositoryExecutor;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;


@Command(name = "repository")
public class RepositoryCommand extends io.dangernoodle.grt.Command.DefinitionOrAll
{
    private static final String CLEARWEBHOOKS = "clearWebhooks";
     
    @Option(names = "--" + CLEARWEBHOOKS)
    private boolean clearWebhooks;

    @Option(names = "--" + "ignoreErrors")
    private boolean igoreErrors;

    public RepositoryCommand(Injector injector)
    {
        super(injector);
    }

    @Override
    public Map<String, Object> asMap()
    {
        return Map.of(CLEARWEBHOOKS, clearWebhooks);
    }

    @Override
    public boolean ignoreErrors()
    {
        return igoreErrors;
    }

    @Override
    protected Class<? extends io.dangernoodle.grt.Command.Executor> getExecutor()
    {
        return RepositoryExecutor.class;
    }
}
