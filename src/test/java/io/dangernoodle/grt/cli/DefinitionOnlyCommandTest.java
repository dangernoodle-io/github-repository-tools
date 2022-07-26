package io.dangernoodle.grt.cli;

import org.junit.jupiter.api.Test;

import io.dangernoodle.grt.Command;


public class DefinitionOnlyCommandTest extends AbstractCommandTest<Command>
{
    @Test
    public void testDefinition()
    {
        givenADefinition();
        whenParseArguments();
        thenDefinitionMatches();
        thenAutoAddToWorkflowEnabled();
    }
    
    @Test
    public void testSchemaDisabled()
    {
        givenADefinition();
        givenDisableSchema();
        whenParseArguments();
        thenDefinitionMatches();
        thenAutoAddToWorkflowEnabled();
        thenDisableSchemaEnabled();
    }

    @Test
    public void testRequired()
    {
        thenParseHasErrors();
    }

    @Override
    protected Command createCommand()
    {
        return new DefinitionOnlyCommand(mockInjector);
    }
}
