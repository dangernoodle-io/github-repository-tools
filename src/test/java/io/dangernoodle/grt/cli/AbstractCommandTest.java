package io.dangernoodle.grt.cli;

import static io.dangernoodle.grt.Constants.ENABLE_AUTO_ADD_WORKFLOW;
import static io.dangernoodle.grt.Constants.IGNORE_ERRORS_OPT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Injector;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.dangernoodle.grt.Command;
import picocli.CommandLine;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.ParseResult;


public abstract class AbstractCommandTest<T extends Command>
{
    protected List<String> args;
    
    protected T command;

    @Mock
    protected Injector mockInjector;

    protected ParseResult parseResult;

    private CommandLine commandLine;
    
    protected String expectedDef;

    @BeforeEach
    public void beforeEach()
    {
        MockitoAnnotations.initMocks(this);

        args = new ArrayList<>();
        
        command = createCommand();
        commandLine = new CommandLine(command);
    }

    protected abstract T createCommand();

    protected String createOption(String name, String value)
    {
        return String.format("%s=%s", name, value);
    }
    
    protected void givenADefinition()
    {
        expectedDef = "definition";
        args.add(expectedDef);
    }
    
    protected void givenIgnoreErrors()
    {
        args.add(IGNORE_ERRORS_OPT);
    }
    
    protected void thenAutoAddToWorkflowDisabled()
    {
        assertTrue(command.toArgMap().containsKey(ENABLE_AUTO_ADD_WORKFLOW));
        assertEquals(false, command.toArgMap().get(ENABLE_AUTO_ADD_WORKFLOW));
    }
    
    protected void thenAutoAddToWorkflowEnabled()
    {
        assertTrue(command.toArgMap().containsKey(ENABLE_AUTO_ADD_WORKFLOW));
        assertEquals(true, command.toArgMap().get(ENABLE_AUTO_ADD_WORKFLOW));
    }
    
    protected void thenDefinitionMatches()
    {
        assertEquals(expectedDef, command.getDefinition());
    }
    
    protected void thenIgnoreErrorsDisabled()
    {
        assertFalse(command.ignoreErrors());
    }
    
    protected void thenIgnoreErrorsEnabled()
    {
        assertTrue(command.ignoreErrors());
    }
    
    protected void thenParseHasErrors()
    {
        assertThrows(ParameterException.class, () -> whenParseArguments());
    }

    protected void whenParseArguments()
    {
        parseResult = commandLine.parseArgs(args.toArray(String[]::new));
    }
}
