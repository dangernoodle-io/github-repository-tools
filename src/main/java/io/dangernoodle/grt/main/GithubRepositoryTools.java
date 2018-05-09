package io.dangernoodle.grt.main;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.dangernoodle.grt.cli.CommandLineDelegate;
import io.dangernoodle.grt.cli.CommandLineDelegate.Executor;


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
        catch (@SuppressWarnings("unused") IllegalArgumentException e)
        {
            // no-op. thrown/catch so the container can shutdown cleanly
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

    private static Class<? extends Executor> getCommandClass(WeldContainer container, String... args)
    {
        return container.select(CommandLineDelegate.class)
                        .get()
                        .parse(args)
                        .getCommandExectorClass();
    }
}
