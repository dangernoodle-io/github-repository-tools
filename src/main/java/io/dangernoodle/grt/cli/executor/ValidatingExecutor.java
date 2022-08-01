package io.dangernoodle.grt.cli.executor;

import com.google.inject.Injector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.dangernoodle.grt.Command;
import io.dangernoodle.grt.cli.ValidateCommand;


/**
 * {@link DefinitionExecutor} delegate that invokes the definition validation before proceeding.
 * 
 * @since 0.9.0
 */
public class ValidatingExecutor extends CommandExecutor
{
    private static final Logger logger = LoggerFactory.getLogger(ValidatingExecutor.class);

    private final Injector injector;

    public ValidatingExecutor(Injector injector)
    {
        this.injector = injector;
    }

    @Override
    public void execute(Command command) throws Exception
    {
        String definition = null;
        if (command.disableSchema())
        {
            definition = command.getDefinition();
            logger.warn("schema validation (excluding [{}]) disabled!", definition);
        }

        new ValidateCommand(injector, definition).call();
        injector.getInstance(DefinitionExecutor.class)
                .execute(command);
    }
    
    
}
