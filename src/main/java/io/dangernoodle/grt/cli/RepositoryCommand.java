package io.dangernoodle.grt.cli;

import java.util.Map;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import io.dangernoodle.grt.Workflow;
import io.dangernoodle.grt.utils.RepositoryFactory;


@Parameters(commandNames = "repository", resourceBundle = "GithubRepositoryTools", commandDescriptionKey = "repository")
public class RepositoryCommand implements CommandLineParser.Command
{
    @Parameter(descriptionKey = "all", names = "--all")
    private static boolean all;

    @Parameter(descriptionKey = "ignoreErrors", names = "--ignoreErrors")
    private static boolean ignoreErrors;

    @Parameter(descriptionKey = "clearWebhooks", names = "--clearWebhooks")
    private static boolean clearWebhooks;

    @Parameter(descriptionKey = "name", required = true)
    private static String name;

    @Override
    public boolean isIgnoreErrors()
    {
        return ignoreErrors;
    }
    
    @Override
    public Class<? extends Executor> getCommandExectorClass()
    {
        return Executor.class;
    }

    public static class Executor extends CommandLineExecutor.RepositoryExecutor
    {
        public Executor(RepositoryFactory factory, Workflow workflow)
        {
            super(factory, workflow);
        }

        @Override
        protected Map<String, Object> getArguments()
        {
            return Map.of("clearWebhooks", clearWebhooks);
        }

        @Override
        protected String getRepositoryName()
        {
            return all ? "*" : name;
        }

//        @Override
//        protected boolean isIgnoreErrors()
//        {
//            return ignoreErrors;
//        }
    }
}
