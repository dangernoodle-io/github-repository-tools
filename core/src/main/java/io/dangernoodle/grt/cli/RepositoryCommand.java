package io.dangernoodle.grt.cli;

import java.io.File;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import io.dangernoodle.grt.Arguments;
import io.dangernoodle.grt.Repository;
import io.dangernoodle.grt.internal.RepositoryMerger;
import io.dangernoodle.grt.internal.WorkflowExecutor;


@ApplicationScoped
@Parameters(commandNames = "repository", commandDescription = "create or update a repository")
public class RepositoryCommand implements CommandLineDelegate.Command
{
    @Parameter(description = "repository name", required = true)
    private static String name;

    @Override
    public Class<? extends CommandLineDelegate.Executor> getCommandExectorClass()
    {
        return Executor.class;
    }

    @ApplicationScoped
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
            RepositoryMerger merger = new RepositoryMerger(Repository.load(defaults), Repository.load(overrides));
            Repository repository = merger.merge();

            workflow.execute(repository);
        }

        @Override
        protected String getRepositoryName()
        {
            return name;
        }
    }
}
