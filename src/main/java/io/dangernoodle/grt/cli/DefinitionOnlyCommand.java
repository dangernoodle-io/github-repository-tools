package io.dangernoodle.grt.cli;

import static io.dangernoodle.grt.Constants.DEFINITION;

import com.google.inject.Injector;

import io.dangernoodle.grt.Command;
import picocli.CommandLine.Parameters;


/**
 * Base class for commands that work against a single definition file
 * 
 * @since 0.9.0
 */
public class DefinitionOnlyCommand extends Command
{
    @Parameters(descriptionKey = DEFINITION, index = "0")
    private String definition;

    protected DefinitionOnlyCommand(Injector injector)
    {
        super(injector);
    }

    @Override
    public String getDefinition()
    {
        return definition;
    }
}
