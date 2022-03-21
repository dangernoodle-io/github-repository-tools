package io.dangernoodle.grt.cli.options;

import static io.dangernoodle.grt.Constants.PASSWORD;
import static io.dangernoodle.grt.Constants.PASSWORD_OPT;
import static io.dangernoodle.grt.Constants.USERNAME;
import static io.dangernoodle.grt.Constants.USERNAME_OPT;

import java.util.Map;

import picocli.CommandLine.Option;


/**
 * Represents a <code>username</code> and <code>password</code> as mutually dependent options.
 *
 * <pre>
 * &#64;ArgGroup(exclusive = false, multiplicity = "1")
 * private UserPassOption userPass;
 * </pre>
 * 
 * @since 0.9.0
 */
public class UserPassOption extends CommandOption
{
    @Option(names = PASSWORD_OPT, arity = "0..1", interactive = true, required = true)
    private char[] password;

    @Option(names = USERNAME_OPT, required = true)
    private String username;

    public char[] getPassword()
    {
        return password;
    }

    public String getUsername()
    {
        return username;
    }

    @Override
    public Map<Object, Object> toArgMap()
    {
        return Map.of(USERNAME, getUsername(), PASSWORD, getPassword());
    }
}
