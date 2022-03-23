package io.dangernoodle.grt.cli;

import static io.dangernoodle.grt.Constants.ALL_OPT;
import static io.dangernoodle.grt.Constants.FILTER_OPT;
import static io.dangernoodle.grt.Constants.WILDCARD;

import org.junit.jupiter.api.Test;

import io.dangernoodle.grt.Command;


public class DefinitionOrAllCommandTest extends AbstractCommandTest<Command>
{
    @Test
    public void testAll()
    {
        givenAllEnabled();
        whenParseArguments();
        thenDefinitionMatches();
        thenAutoAddToWorkflowEnabled();
    }
    
    @Test
    public void testAllFiltered()
    {
        givenAllWithFilter();
        whenParseArguments();
        thenDefinitionMatches();
        thenAutoAddToWorkflowEnabled();
    }

    @Test
    public void testDefinition()
    {
        givenADefinition();
        whenParseArguments();
        thenDefinitionMatches();
        thenAutoAddToWorkflowEnabled();
    }

    @Test
    public void testRequired()
    {
        thenParseHasErrors();
    }

    @Override
    protected Command createCommand()
    {
        return new DefinitionOrAllCommand(mockInjector);
    }

    private void givenAllEnabled()
    {
        expectedDef = WILDCARD;
        args.add(ALL_OPT);
    }

    private void givenAllWithFilter()
    {
        expectedDef = "conductor/" + WILDCARD;

        args.add(ALL_OPT);
        args.add(createOption(FILTER_OPT, "conductor"));
    }
}
