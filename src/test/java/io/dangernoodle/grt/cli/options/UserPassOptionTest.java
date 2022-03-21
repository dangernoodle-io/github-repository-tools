package io.dangernoodle.grt.cli.options;

import static io.dangernoodle.grt.Constants.PASSWORD;
import static io.dangernoodle.grt.Constants.PASSWORD_OPT;
import static io.dangernoodle.grt.Constants.USERNAME;
import static io.dangernoodle.grt.Constants.USERNAME_OPT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import picocli.CommandLine.ArgGroup;

public class UserPassOptionTest extends AbstractOptionTest
{
    @ArgGroup(exclusive = false, multiplicity = "1")
    private UserPassOption userPass;
    
    @Test
    public void testRequired()
    {
        thenParseHasErrors();
    }
    
    @Test
    public void testUserPass()
    {
        givenUserAndPass();
        whenParseArguments();
        thenUserPassInMap();
    }

    private void givenUserAndPass()
    {
        args.add(createOption(PASSWORD_OPT, PASSWORD));
        args.add(createOption(USERNAME_OPT, USERNAME));
    }

    private void thenUserPassInMap()
    {
        assertEquals(2, userPass.toArgMap().size());
        assertTrue(userPass.toArgMap().containsKey(PASSWORD));
        assertEquals(USERNAME, userPass.toArgMap().get(USERNAME));
    }    
}
