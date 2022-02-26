package io.dangernoodle.grt.cli;

import static io.dangernoodle.grt.Constants.VALIDATE;

import com.google.inject.Injector;

import io.dangernoodle.grt.cli.exector.ValidateExecutor;
import picocli.CommandLine.Command;


@Command(name = VALIDATE)
public class ValidateCommand extends io.dangernoodle.grt.Command
{
    public ValidateCommand(Injector injector)
    {
        super(injector);
    }

    @Override
    protected Class<? extends io.dangernoodle.grt.Command.Executor> getExecutor()
    {
        return ValidateExecutor.class;
    }
}
