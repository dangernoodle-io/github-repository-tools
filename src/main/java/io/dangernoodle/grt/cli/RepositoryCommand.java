package io.dangernoodle.grt.cli;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import io.dangernoodle.grt.internal.FileLoader;
import io.dangernoodle.grt.internal.WorkflowExecutor;
import io.dangernoodle.grt.utils.RepositoryMerger;


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
    public Class<? extends Executor> getCommandExectorClass()
    {
        return Executor.class;
    }

    public static class Executor extends CommandLineExecutor.RepositoryFileExecutor
    {
        private final WorkflowExecutor workflowExecutor;

        private final RepositoryMerger repositoryMerger;

        public Executor(WorkflowExecutor workflowExecutor, RepositoryMerger repositoryMerger, FileLoader fileLoader)
        {
            super(fileLoader);

            this.repositoryMerger = repositoryMerger;
            this.workflowExecutor = workflowExecutor;
        }

        @Override
        protected void execute(File defaults, File overrides) throws Exception
        {
            workflowExecutor.execute(repositoryMerger.merge(overrides, defaults),
                    Collections.singletonMap("clearWebhooks", clearWebhooks));
        }

        @Override
        protected Collection<File> getRepositories() throws IOException
        {
            if (all)
            {
                return fileLoader.loadRepositories(name);
            }

            return super.getRepositories();
        }

        @Override
        protected String getRepositoryName()
        {
            return name;
        }

        @Override
        protected boolean isIgnoreErrors()
        {
            return ignoreErrors;
        }

        @Override
        protected void postExecution() throws Exception
        {
            workflowExecutor.postExecution();
        }

        @Override
        protected void preExecution() throws Exception
        {
            workflowExecutor.preExecution();
        }
    }
}
