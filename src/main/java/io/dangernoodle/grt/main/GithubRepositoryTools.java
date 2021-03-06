package io.dangernoodle.grt.main;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.dangernoodle.grt.cli.CommandLineExecutor;
import io.dangernoodle.grt.cli.CommandLineParser;
import io.dangernoodle.grt.utils.JsonValidationException;


public class GithubRepositoryTools
{
    private static final Logger logger = LoggerFactory.getLogger(GithubRepositoryTools.class);

    public static void main(String... args) throws Exception
    {
        Weld weld = new Weld();
        WeldContainer container = weld.initialize();

        try
        {
            container.select(getCommandClass(container, args))
                     .get()
                     .execute();
        }
        catch (@SuppressWarnings("unused") IllegalArgumentException | JsonValidationException e)
        {
            // no-op so the container can shutdown cleanly, these have already been logged accordingly
        }
        catch (Exception e)
        {
            // catch and log any exceptions that make it this far so we shutdown gracefully
            logger.error("an unexpected error has occurred", e);
        }
        finally
        {
            container.shutdown();
        }
    }

    private static Class<? extends CommandLineExecutor> getCommandClass(WeldContainer container, String... args)
    {
        return container.select(CommandLineParser.class)
                        .get()
                        .parse(args)
                        .getCommandExectorClass();
    }
}
