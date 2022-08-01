package io.dangernoodle.grt.cli;

import static io.dangernoodle.grt.Constants.VALIDATE;
import static io.dangernoodle.grt.Constants.WILDCARD;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import com.google.inject.Injector;

import io.dangernoodle.grt.cli.executor.CommandExecutor;
import io.dangernoodle.grt.cli.executor.ValidationExecutor;
import picocli.CommandLine.Command;


@Command(name = VALIDATE)
public class ValidateCommand extends io.dangernoodle.grt.Command
{
    private final String definition;

    public ValidateCommand(Injector injector)
    {
        this(injector, null);
    }

    public ValidateCommand(Injector injector, String definition)
    {
        super(injector);
        this.definition = definition;
    }

    @Override
    public String getDefinition()
    {
        return Optional.ofNullable(definition)
                       .orElse(WILDCARD);
    }

    @Override
    public Map<Object, Object> toArgMap()
    {
        return Collections.emptyMap();
    }

    @Override
    protected Class<? extends CommandExecutor> getExecutor()
    {
        return ValidationExecutor.class;
    }
}
