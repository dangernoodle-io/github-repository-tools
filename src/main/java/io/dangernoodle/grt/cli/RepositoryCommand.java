package io.dangernoodle.grt.cli;

import static io.dangernoodle.grt.Constants.CLEAR_WEBHOOKS;
import static io.dangernoodle.grt.Constants.CLEAR_WEBHOOKS_OPT;
import static io.dangernoodle.grt.Constants.IGNORE_ERRORS_OPT;
import static io.dangernoodle.grt.Constants.REPOSITORY;

import java.util.Map;

import com.google.inject.Injector;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;


@Command(name = REPOSITORY)
public class RepositoryCommand extends DefinitionOrAllCommand
{
    @Option(names = CLEAR_WEBHOOKS_OPT)
    private boolean clearWebhooks;

    @Option(names = IGNORE_ERRORS_OPT)
    private boolean igoreErrors;

    public RepositoryCommand(Injector injector)
    {
        super(injector);
    }

    @Override
    public boolean ignoreErrors()
    {
        return igoreErrors;
    }

    @Override
    public Map<Object, Object> toArgMap()
    {
        return merge(super.toArgMap(), Map.of(CLEAR_WEBHOOKS, clearWebhooks));
    }
}
