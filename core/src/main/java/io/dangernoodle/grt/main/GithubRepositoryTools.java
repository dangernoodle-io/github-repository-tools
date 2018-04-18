package io.dangernoodle.grt.main;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

import io.dangernoodle.grt.cli.CommandLineDelegate;
import io.dangernoodle.grt.cli.CommandLineDelegate.Executor;


public class GithubRepositoryTools
{
    public static void main(String... args) throws Exception
    {
        Weld weld = new Weld();
        WeldContainer container = weld.initialize();

        container.select(getCommandClass(container, args))
                 .get()
                 .execute();

        container.shutdown();
    }

    private static Class<? extends Executor> getCommandClass(WeldContainer container, String... args)
    {
        return container.select(CommandLineDelegate.class)
                        .get()
                        .parse(args)
                        .getCommandExectorClass();
    }
}
