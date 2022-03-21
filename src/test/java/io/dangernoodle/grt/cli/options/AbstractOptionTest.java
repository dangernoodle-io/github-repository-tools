package io.dangernoodle.grt.cli.options;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.ParseResult;


@Command
public abstract class AbstractOptionTest
{
    protected ParseResult parseResult;

    private CommandLine commandLine;

    protected List<String> args;

    @BeforeEach
    public void beforeEach()
    {
        args = new ArrayList<>();
        commandLine = new CommandLine(this);
    }

    protected void whenParseArguments()
    {
        parseResult = commandLine.parseArgs(args.toArray(String[]::new));
    }
    
    protected void thenParseHasErrors()
    {
        assertThrows(ParameterException.class, () -> whenParseArguments());
    }
    
    protected String createOption(String name, String value)
    {
        return String.format("%s=%s", name, value);
    }
}
