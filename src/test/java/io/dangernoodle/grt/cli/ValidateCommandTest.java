package io.dangernoodle.grt.cli;

import static io.dangernoodle.grt.Constants.WILDCARD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;


public class ValidateCommandTest extends AbstractCommandTest<ValidateCommand>
{
    @Test
    public void testCommand()
    {
        whenParseArguments();
        thenDefintionIsWildcard();
        thenArgMapIsEmpty();
    }

    @Override
    protected ValidateCommand createCommand()
    {
        return new ValidateCommand(mockInjector);
    }

    private void thenArgMapIsEmpty()
    {
        assertTrue(command.toArgMap().isEmpty());
    }

    private void thenDefintionIsWildcard()
    {
        assertEquals(WILDCARD, command.getDefinition());
    }
}
