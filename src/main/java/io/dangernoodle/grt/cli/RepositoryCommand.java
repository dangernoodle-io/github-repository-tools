package io.dangernoodle.grt.cli;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import io.dangernoodle.grt.Arguments;
import io.dangernoodle.grt.Repository;
import io.dangernoodle.grt.internal.RepositoryMerger;
import io.dangernoodle.grt.internal.WorkflowExecutor;


@Parameters(commandNames = "repository", resourceBundle = "GithubRepositoryTools", commandDescriptionKey = "repository")
public class RepositoryCommand implements CommandLineDelegate.Command
{
    @Parameter(descriptionKey = "repoName", required = true)
    private static String name;

    @Override
    public Class<? extends CommandLineDelegate.Executor> getCommandExectorClass()
    {
        return Executor.class;
    }

    public static class Executor extends CommandLineDelegate.RepositoryExecutor
    {
        private final WorkflowExecutor workflow;

        @Inject
        public Executor(Arguments arguments, WorkflowExecutor workflow)
        {
            super(arguments);
            this.workflow = workflow;
        }

        @Override
        protected void execute(File defaults, File overrides) throws Exception
        {
            RepositoryMerger merger = createRepositoryMerger(defaults, overrides);
            Repository repository = merger.merge();

            workflow.execute(repository);
        }

        @Override
        protected String getRepositoryName()
        {
            return name;
        }

        RepositoryMerger createRepositoryMerger(File defaults, File overrides) throws IOException
        {
            return new RepositoryMerger(Repository.load(defaults), Repository.load(overrides));
        }
    }
}
