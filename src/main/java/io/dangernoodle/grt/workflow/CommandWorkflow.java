package io.dangernoodle.grt.workflow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.dangernoodle.grt.Repository;
import io.dangernoodle.grt.Workflow;


/**
 * Executes one or more <code>Workflow</code>s for a command.
 * 
 * <pre>
 * {
 *   "workflows": {
 *     "command1": ["workflow1"],
 *     "command2": ["workflow1", "workflow2"]    
 *   }
 * }
 * </pre>
 * 
 * @since 0.9.0
 */
public class CommandWorkflow implements Workflow<Repository>
{
    private final String command;

    private final boolean ignoreErrors;

    private final Map<String, Workflow<Repository>> workflows;

    public CommandWorkflow(String command, boolean ignoreErrors, Collection<Workflow<Repository>> workflows)
    {
        this.command = command;
        this.ignoreErrors = ignoreErrors;
        this.workflows = workflows.stream()
                                  .collect(Collectors.toMap(workflow -> workflow.getName(), Function.identity()));
    }

    @Override
    public void execute(Repository repository, Context context) throws Exception
    {
        Collection<Workflow<Repository>> workflows = getWorkflows(repository, context.isAutoAddWorkflowEnabled());
        CompositeWorkflow<Repository> delegate = new CompositeWorkflow<>(ignoreErrors, workflows);

        try
        {
            delegate.preExecution();
            delegate.execute(repository, context);
        }
        finally
        {
            delegate.postExecution();
        }
    }

    public String getCommand()
    {
        return command;
    }

    private Workflow<Repository> getWorkflow(String name)
    {
        return Optional.ofNullable(workflows.get(name))
                       .orElseThrow(() -> new IllegalStateException("failed to find workflow named [" + name + "]"));
    }

    private Collection<Workflow<Repository>> getWorkflows(Repository repository, boolean autoAdd)
    {
        ArrayList<String> names = new ArrayList<>(repository.getWorkflows(command));

        /*
         * the command should have a corresponding workflow of the same name, otherwise add it. this allows new commands
         * to be added w/o needing to update configuration files.
         */
        if (!names.contains(command) && autoAdd)
        {
            names.add(0, command);
        }

        if (names.isEmpty())
        {
            throw new IllegalStateException("no workflows defined for command [" + command + "]");
        }

        return names.stream()
                    .map(this::getWorkflow)
                    .collect(Collectors.toList());
    }
}
