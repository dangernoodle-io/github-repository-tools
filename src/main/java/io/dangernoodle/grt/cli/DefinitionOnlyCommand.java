package io.dangernoodle.grt.cli;

import com.google.inject.Injector;

import io.dangernoodle.grt.Command;
import io.dangernoodle.grt.cli.executor.CommandExecutor;
import io.dangernoodle.grt.cli.executor.ValidatingExecutor;
import picocli.CommandLine.Parameters;


/**
 * Base class for commands that work against a single definition file
 * 
 * @since 0.9.0
 */
public abstract class DefinitionOnlyCommand extends Command
{
    @Parameters(index = "0")
    private String definition;

    public DefinitionOnlyCommand(Injector injector)
    {
        super(injector);
    }

    @Override
    public String getDefinition()
    {
        return definition;
    }

    @Override
    protected Class<? extends CommandExecutor> getExecutor()
    {
        return ValidatingExecutor.class;
    }
}
