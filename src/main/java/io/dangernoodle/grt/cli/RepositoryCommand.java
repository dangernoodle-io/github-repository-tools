package io.dangernoodle.grt.cli;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import javax.inject.Inject;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import io.dangernoodle.grt.Arguments;
import io.dangernoodle.grt.Repository;
import io.dangernoodle.grt.Workflow;
import io.dangernoodle.grt.internal.WorkflowExecutor;
import io.dangernoodle.grt.utils.JsonTransformer;
import io.dangernoodle.grt.utils.RepositoryMerger;


@Parameters(commandNames = "repository", resourceBundle = "GithubRepositoryTools", commandDescriptionKey = "repository")
public class RepositoryCommand implements CommandLineParser.Command
{
    @Parameter(descriptionKey = "all", names = "--all")
    private static boolean all;

    @Parameter(descriptionKey = "ignoreErrors", names = "--ignoreErrors")
    private static boolean ignoreErrors;

    @Parameter(descriptionKey = "name", required = true)
    private static String name;

    @Override
    public Class<? extends Executor> getCommandExectorClass()
    {
        return Executor.class;
    }

    public static class Executor extends CommandLineExecutor.RepositoryFileExecutor
    {
        private final Collection<Workflow.PrePost> prePost;

        private final WorkflowExecutor workflow;

        @Inject
        public Executor(Arguments arguments, WorkflowExecutor workflow, JsonTransformer transformer, Collection<Workflow.PrePost> prePost)
        {
            super(arguments, transformer);

            this.workflow = workflow;
            this.prePost = prePost;
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
        protected Collection<File> getRepositories() throws IOException
        {
            if (all)
            {
                return loader.loadRepositories(name);
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
            for (Workflow.PrePost toExecute : prePost)
            {
                toExecute.postExecution();
            }
        }

        @Override
        protected void preExecution() throws Exception
        {
            for (Workflow.PrePost toExecute : prePost)
            {
                toExecute.preExecution();
            }
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
