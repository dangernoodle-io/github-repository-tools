package io.dangernoodle.grt.workflow;

import static io.dangernoodle.grt.Constants.WILDCARD;

import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;

import io.dangernoodle.grt.Workflow;


public class LifecycleWorkflow<T> implements Workflow<T>
{
    private final String command;

    private final Collection<Workflow.Lifecycle> lifecycles;

    private final Workflow<T> delegate;

    public LifecycleWorkflow(Workflow<T> delegate, Set<Workflow.Lifecycle> lifecycles)
    {
        this(delegate, lifecycles, WILDCARD);
    }

    public LifecycleWorkflow(Workflow<T> delegate, Set<Workflow.Lifecycle> lifecycles, String command)
    {
        this.delegate = delegate;
        this.lifecycles = lifecycles;
        this.command = command;
    }

    @Override
    public void postExecution()
    {
        executeLifecycle(Workflow.Lifecycle::postExecution);
    }

    @Override
    public void preExecution()
    {
        executeLifecycle(Workflow.Lifecycle::preExecution);
    }
    
    private void executeLifecycle(Consumer<Workflow.Lifecycle> lifecycle)
    {
        lifecycles.stream()
                  .filter(l -> l.getCommands().contains(command))
                  .forEach(lifecycle);
    }

    @Override
    public void execute(T object, Context context) throws Exception
    {
        try
        {
            delegate.preExecution();
            delegate.execute(object, context);
        }
        finally
        {
            delegate.postExecution();
        }
    }
}
