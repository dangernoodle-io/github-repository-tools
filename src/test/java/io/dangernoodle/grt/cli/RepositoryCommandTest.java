package io.dangernoodle.grt.cli;

import static io.dangernoodle.grt.Constants.CLEAR_WEBHOOKS;
import static io.dangernoodle.grt.Constants.CLEAR_WEBHOOKS_OPT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;


public class RepositoryCommandTest extends AbstractCommandTest<RepositoryCommand>
{
    @Test
    public void testClearWebhooks()
    {
        givenADefinition();
        givenClearWebhooks();
        whenParseArguments();
        thenDefinitionMatches();
        thenAutoAddToWorkflowEnabled();
        thenClearWebooksEnabled();

    }
    
    @Test
    public void testIgnoreErrors()
    {
        givenADefinition();
        givenIgnoreErrors();
        whenParseArguments();
        thenDefinitionMatches();
        thenAutoAddToWorkflowEnabled();
        thenIgnoreErrorsEnabled();
    }
    
    @Test
    public void testNoOptions()
    {
        givenADefinition();
        whenParseArguments();
        thenDefinitionMatches();
        thenAutoAddToWorkflowEnabled();
        thenIgnoreErrorsDisabled();
        thenClearWebooksDisabled();
    }
    
    @Test
    public void testRequired()
    {
        thenParseHasErrors();
    }

    @Override
    protected RepositoryCommand createCommand()
    {
        return new RepositoryCommand(mockInjector);
    }

    private void givenClearWebhooks()
    {
        args.add(CLEAR_WEBHOOKS_OPT);
    }

    private void thenClearWebooksDisabled()
    {
        assertTrue(command.toArgMap().containsKey(CLEAR_WEBHOOKS));
        assertEquals(false, command.toArgMap().get(CLEAR_WEBHOOKS));
    }

    private void thenClearWebooksEnabled()
    {
        assertTrue(command.toArgMap().containsKey(CLEAR_WEBHOOKS));
        assertEquals(true, command.toArgMap().get(CLEAR_WEBHOOKS));
    }
}
