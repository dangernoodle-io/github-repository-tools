package io.dangernoodle.grt.workflows;

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
 * <p>
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
public class CommandWorkflow implements Workflow
{
    private final String command;

    private final boolean ignoreErrors;

    private final Map<String, Workflow> workflows;

    public CommandWorkflow(String command, Collection<Workflow> workflows, boolean ignoreErrors)
    {
        this.command = command;
        this.ignoreErrors = ignoreErrors;
        this.workflows = workflows.stream()
                                  .collect(Collectors.toMap(workflow -> workflow.getName(), Function.identity()));
    }

    @Override
    public void execute(Repository repository, Context context) throws Exception
    {
        Collection<Workflow> workflows = getWorkflows(repository);
        ChainedWorkflow delegate = new ChainedWorkflow(workflows, ignoreErrors);

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

    private Collection<Workflow> getWorkflows(Repository repository)
    {
        ArrayList<String> names = new ArrayList<>(repository.getWorkflows(command));

        /*
         * the command should have a corresponding workflow of the same name, otherwise add it. this allows new commands
         * to be added w/o needing to update configuration files.
         */
        if (!names.contains(command))
        {
            names.add(0, command);
        }

        return names.stream()
                    .map(this::getWorkflow)
                    .collect(Collectors.toList());
    }

    private Workflow getWorkflow(String name)
    {
        return Optional.ofNullable(workflows.get(name))
                       .orElseThrow(() -> new IllegalStateException("failed to find Workflow for [" + name + "]"));
    }

}
