package io.dangernoodle.grt.cli;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

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
    public static class Executor implements CommandLineDelegate.Executor
    {
        private final String root;

        private final WorkflowExecutor workflow;

        @Inject
        public Executor(Arguments arguments, WorkflowExecutor workflow)
        {
            this.root = arguments.getRoot();
            this.workflow = workflow;
        }

        @Override
        public void execute() throws Exception
        {
            File defaults = new File(root + File.separator + "configuration.json");
            File overrides = findRepositoryFile(name);

            RepositoryMerger merger = new RepositoryMerger(Repository.load(defaults), Repository.load(overrides));
            Repository repository = merger.merge();

            workflow.execute(repository);
        }

        private File findRepositoryFile(String name) throws IOException, IllegalStateException
        {
            List<Path> files = Files.find(Paths.get(root), 10, (path, attrs) -> {
                return path.getFileName().toString().equals(name + ".json");
            }).collect(Collectors.toList());

            if (files.size() == 0)
            {
                throw new IllegalStateException("failed to find file for repository [" + name + "]");
            }

            if (files.size() > 1)
            {
                throw new IllegalStateException("multiple repsository files named [" + name + "] found");
            }

            return files.get(0).toFile();
        }
    }
}
