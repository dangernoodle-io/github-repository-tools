package io.dangernoodle.grt.cli;

import java.io.File;
import java.util.Collection;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.UnixStyleUsageFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.dangernoodle.grt.Arguments;
import io.dangernoodle.grt.FileLoader;


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

    public static abstract class Executor
    {
        protected final Logger logger;

        protected final FileLoader loader;

        public Executor(Arguments arguments)
        {
            this.loader = new FileLoader(arguments.getRepoDir());
            this.logger = LoggerFactory.getLogger(getClass());
        }

        public abstract void execute() throws Exception;
    }

    public static abstract class RepositoryExecutor extends Executor
    {
        public RepositoryExecutor(Arguments arguments)
        {
            super(arguments);
        }

        @Override
        public void execute() throws Exception
        {
            File defaults = loader.loadRepositoryDefaults();
            File overrides = loader.loadRepository(getRepositoryName());

            execute(defaults, overrides);
        }

        protected abstract void execute(File defaults, File overrides) throws Exception;

        protected abstract String getRepositoryName();
    }
}
