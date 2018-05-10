package io.dangernoodle.grt.cli;

import java.util.Collection;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.UnixStyleUsageFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.dangernoodle.grt.Arguments;


public class CommandLineParser
{
    private static final Logger logger = LoggerFactory.getLogger(CommandLineParser.class);

    private final JCommander jCommander;

    public CommandLineParser(Collection<Command> commands, Arguments arguments)
    {
        this.jCommander = createJCommander(arguments);

        jCommander.setProgramName("github-repository-tools");
        jCommander.setUsageFormatter(new UnixStyleUsageFormatter(jCommander));

        addCommands(commands);
    }

    public Command parse(String... args) throws IllegalArgumentException
    {
        return (Command) jCommander.getCommands()
                                   .get(parseArguments(args))
                                   .getObjects()
                                   .get(0);
    }

    // visible for testing
    JCommander createJCommander(Arguments arguments)
    {
        return new JCommander(arguments);
    }

    private void addCommands(Collection<Command> commands)
    {
        commands.forEach(command -> {
            logger.trace("adding discovered command [{}]", command.getClass());
            jCommander.addCommand(command);
        });
    }

    private String parseArguments(String... args) throws IllegalArgumentException
    {
        try
        {
            jCommander.parse(args);
            String parsed = jCommander.getParsedCommand();
            if (parsed == null)
            {
                throw new ParameterException("No command was specified");
            }

            return parsed;
        }
        catch (ParameterException e)
        {
            logger.error("{}\n", e.getMessage());
            jCommander.usage();

            throw new IllegalArgumentException();
        }
    }

    public interface Command
    {
        Class<? extends CommandLineExecutor> getCommandExectorClass();
    }
}
