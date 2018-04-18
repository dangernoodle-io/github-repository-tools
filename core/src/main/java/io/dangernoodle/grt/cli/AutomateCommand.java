package io.dangernoodle.grt.cli;

import java.io.File;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import io.dangernoodle.grt.Arguments;
import io.dangernoodle.grt.internal.WorkflowExecutor;


@ApplicationScoped
@Parameters(commandNames = "automate", commandDescription = "")
public class AutomateCommand implements CommandLineDelegate.Command
{
    @Parameter
    private static String file;

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
            workflow.execute(new File(root + File.separator + file));
        }
    }
}
