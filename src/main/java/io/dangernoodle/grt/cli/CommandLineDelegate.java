package io.dangernoodle.grt.cli;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.UnixStyleUsageFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.dangernoodle.grt.Arguments;


public class CommandLineDelegate
{
    private static final Logger logger = LoggerFactory.getLogger(CommandLineDelegate.class);

    private final JCommander jCommander;

    public CommandLineDelegate(Collection<Command> commands, Arguments arguments)
    {
        // see ParametersProducer#get
        this.jCommander = new JCommander(arguments);

        jCommander.setUsageFormatter(new UnixStyleUsageFormatter(jCommander));
        jCommander.setProgramName("github-repository-tools");

        addCommands(commands);
    }

    public Command parse(String... args) throws IllegalArgumentException
    {
        parseArguments(args);
        return (Command) jCommander.getCommands()
                                   .get(jCommander.getParsedCommand())
                                   .getObjects()
                                   .get(0);
    }

    private void addCommands(Collection<Command> commands)
    {
        commands.forEach(command -> {
            logger.trace("adding discovered command [{}]", command.getClass());
            jCommander.addCommand(command);
        });
    }

    private void parseArguments(String... args) throws IllegalArgumentException
    {
        try
        {
            jCommander.parse(args);
        }
        catch (ParameterException e)
        {
            logger.error("{}", e.getMessage());
            System.out.println();

            jCommander.usage();

            throw new IllegalArgumentException();
        }
    }

    public interface Command
    {
        Class<? extends Executor> getCommandExectorClass();
    }

    public interface Executor
    {
        void execute() throws Exception;
    }

    public static abstract class RepositoryExecutor implements Executor
    {
        protected final Logger logger;

        private final String root;

        public RepositoryExecutor(Arguments arguments)
        {
            this.root = arguments.getRepoDir();
            this.logger = LoggerFactory.getLogger(getClass());
        }

        @Override
        public void execute() throws Exception
        {
            // depth = 1 for the configuration file, it should be at top level of root directory
            File defaults = findRepositoryFile(root, 1, "github-repository-tools");
            // depth = 10 is somewhat arbitrary - can be increased if there is ever a need
            File overrides = findRepositoryFile(root, 10, getName());

            execute(defaults, overrides);
        }

        protected abstract void execute(File defaults, File overrides) throws Exception;

        protected abstract String getRepositoryName();

        private File findRepositoryFile(String root, int depth, String name) throws IOException, IllegalStateException
        {
            List<Path> files = Files.find(Paths.get(root), depth, (path, attrs) -> {
                return path.getFileName().toString().equals(name + ".json");
            }).collect(Collectors.toList());

            if (files.size() == 0)
            {
                throw new IllegalStateException("failed to find repository file [" + name + "]");
            }

            if (files.size() > 1)
            {
                throw new IllegalStateException("multiple repsository files named [" + name + "] found");
            }

            return files.get(0).toFile();
        }

        private String getName()
        {
            return getRepositoryName().replace('.', '-');
        }
    }
}
