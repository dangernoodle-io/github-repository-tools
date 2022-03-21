package io.dangernoodle.grt.cli.options;

import java.util.Map;

import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Spec;


/**
 * @since 0.9.0
 */
public abstract class CommandOption
{
    @Spec
    private CommandSpec spec;

    public abstract Map<Object, Object> toArgMap();

    protected ParameterException createParameterException(String message)
    {
        return new ParameterException(spec.commandLine(), message);
    }
}
