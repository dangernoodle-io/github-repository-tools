package io.dangernoodle.grt.cli.exector;

import com.google.inject.Injector;

import io.dangernoodle.grt.Command;
import io.dangernoodle.grt.cli.ValidateCommand;


/**
 * {@link DefinitionExecutor} delegate that invokes the definition validation before proceeding.
 * 
 * @since 0.9.0
 */
public class ValidatingExecutor extends CommandExecutor
{
    private final ValidateCommand validator;

    private final DefinitionExecutor delegate;

    public ValidatingExecutor(Injector injector)
    {
        this.validator = new ValidateCommand(injector);
        this.delegate = injector.getInstance(DefinitionExecutor.class);
    }

    @Override
    public void execute(Command command) throws Exception
    {
        validator.call();
        delegate.execute(command);
    }
}
