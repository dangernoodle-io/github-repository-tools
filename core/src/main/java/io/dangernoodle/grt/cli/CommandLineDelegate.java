package io.dangernoodle.grt.cli;

import java.util.Collection;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.dangernoodle.grt.Arguments;


public class CommandLineDelegate
{
    private static final Logger logger = LoggerFactory.getLogger(CommandLineDelegate.class);

    private static final Parameters parameters = new Parameters();

    private final JCommander jCommander;

    public CommandLineDelegate(Collection<Command> commands)
    {
        // see ParametersProducer#get
        this.jCommander = new JCommander(parameters);
        addCommands(commands);
    }

    public Command parse(String... args)
    {
        // TODO: usage, error handling

        jCommander.parse(args);
        return (Command) jCommander.getCommands()
                                   .get(jCommander.getParsedCommand())
                                   .getObjects()
                                   .get(0);
    }

    private void addCommands(Collection<Command> commands)
    {
        commands.forEach(command -> {
            logger.debug("adding discovered command [{}]", command.getClass());
            jCommander.addCommand(command);
        });
    }

    public interface Command
    {
        Class<? extends Executor> getCommandExectorClass();
    }

    @ApplicationScoped
    public static class CommandLineProducer
    {
        @Produces
        @ApplicationScoped
        public CommandLineDelegate get(Parameters parameters, Instance<Command> instance)
        {
            return new CommandLineDelegate(instance.stream().collect(Collectors.toList()));
        }
    }

    public interface Executor
    {
        void execute() throws Exception;
    }

    @ApplicationScoped
    public static class ParametersProducer
    {
        @Produces
        @ApplicationScoped
        public Parameters get()
        {
            /*
             * defining the Parameters class as @ApplicationScoped causes JCommander to populate the annotations on the
             * proxy class instead of the actual underlying object, which is no bueno.
             */
            return parameters;
        }
    }

    private static class Parameters implements Arguments
    {
        @Parameter(names = "--root", required = true)
        private String root;

        @Override
        public String getRoot()
        {
            return root;
        }
    }
}
