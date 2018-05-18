package io.dangernoodle.grt.cli;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import io.dangernoodle.grt.Arguments;
import io.dangernoodle.grt.Repository;
import io.dangernoodle.grt.internal.WorkflowExecutor;
import io.dangernoodle.grt.utils.JsonTransformer;
import io.dangernoodle.grt.utils.RepositoryMerger;


@Parameters(commandNames = "repository", resourceBundle = "GithubRepositoryTools", commandDescriptionKey = "repository")
public class RepositoryCommand implements CommandLineParser.Command
{
    @Parameter(descriptionKey = "repoName", required = true)
    private static String name;

    @Override
    public Class<? extends Executor> getCommandExectorClass()
    {
        return Executor.class;
    }

    public static class Executor extends CommandLineExecutor.RepositoryExecutor
    {
        private final WorkflowExecutor workflow;

        @Inject
        public Executor(Arguments arguments, WorkflowExecutor workflow, JsonTransformer transformer)
        {
            super(arguments, transformer);
            this.workflow = workflow;
        }

        @Override
        protected void execute(File defaults, File overrides) throws Exception
        {
            RepositoryMerger merger = createRepositoryMerger();

            Repository deRepo = createRepository(defaults);
            Repository ovRepo = createRepository(overrides);

            Repository repository = merger.merge(ovRepo, deRepo);

            workflow.execute(repository);
        }

        @Override
        protected String getRepositoryName()
        {
            return name;
        }

        Repository createRepository(File file) throws IOException
        {
            return new Repository(transformer.validate(file));
        }

        RepositoryMerger createRepositoryMerger()
        {
            return new RepositoryMerger(transformer);
        }
    }
}
