package io.dangernoodle.grt.cli;

import static io.dangernoodle.grt.Constants.VALIDATE;
import static io.dangernoodle.grt.Constants.WILDCARD;

import java.util.Collections;
import java.util.Map;

import com.google.inject.Injector;

import io.dangernoodle.grt.cli.exector.CommandExecutor;
import io.dangernoodle.grt.cli.exector.ValidationExecutor;
import picocli.CommandLine.Command;


@Command(name = VALIDATE)
public class ValidateCommand extends io.dangernoodle.grt.Command
{
    public ValidateCommand(Injector injector)
    {
        super(injector);
    }

    @Override
    public String getDefinition()
    {
        return WILDCARD;
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
