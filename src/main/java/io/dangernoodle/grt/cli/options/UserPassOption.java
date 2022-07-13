package io.dangernoodle.grt.cli.options;

import static io.dangernoodle.grt.Constants.PASSWORD;
import static io.dangernoodle.grt.Constants.PASSWORD_OPT;
import static io.dangernoodle.grt.Constants.USERNAME;
import static io.dangernoodle.grt.Constants.USERNAME_OPT;

import java.util.Arrays;
import java.util.Map;

import picocli.CommandLine.Option;


/**
 * Represents a <code>username</code> and <code>password</code> as mutually dependent options.
 *
 * <pre>
 * &#64;Mixin
 * private UserPassOption userPass;
 * </pre>
 * 
 * @since 0.9.0
 */
public class UserPassOption extends CommandOption
{
    @Option(names = PASSWORD_OPT, descriptionKey = PASSWORD, interactive = true, required = true)
    private char[] password;

    @Option(names = USERNAME_OPT, descriptionKey = USERNAME, interactive = true, required = true, echo = true)
    private char[] username;

    public void destroy()
    {
        Arrays.fill(password, ' ');
        Arrays.fill(username, ' ');
    }

    public String getPassword()
    {
        return String.valueOf(password);
    }

    public String getUsername()
    {
        return String.valueOf(username);
    }

    @Override
    public Map<Object, Object> toArgMap()
    {
        return Map.of(USERNAME, getUsername(), PASSWORD, getPassword());
    }
}
