package io.dangernoodle.grt.workflow;

import static io.dangernoodle.grt.Constants.WILDCARD;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import io.dangernoodle.grt.Workflow;


/**
 * Executes a workflow that requires lifecycle methods.
 * <p>
 * This workflow is useful for wrapping a {@link ChainedWorkflow} or {@link CompositeWorkflow} that require action to be
 * taken before any workflows run and/or complete.
 * </p>
 *
 * @param <T> workflow object
 * @since 0.9.0
 */
public class LifecycleWorkflow<T> implements Workflow<T>
{
    private final String command;

    private final Workflow<T> delegate;

    private final Collection<Workflow.Lifecycle> lifecycles;

    public LifecycleWorkflow(String command, Workflow<T> delegate, Collection<Workflow.Lifecycle> lifecycles)
    {
        this.delegate = delegate;
        this.lifecycles = lifecycles;
        this.command = command;
    }

    @Override
    public void execute(T object, Context context) throws Exception
    {
        new ChainedWorkflow<>(false, List.of(delegate)).execute(object, context);
    }

    @Override
    public void postExecution()
    {
        try
        {
            executeLifecycle(Workflow.Lifecycle::postExecution);
        }
        catch (Exception e)
        {
            if (e instanceof RuntimeException)
            {
                throw (RuntimeException) e;
            }
        }
    }

    @Override
    public void preExecution() throws Exception
    {
        executeLifecycle(Workflow.Lifecycle::preExecution);
    }

    private void executeLifecycle(Consumer consumer) throws Exception
    {
        Iterator<Workflow.Lifecycle> iterator = lifecycles.stream()
                                                          .filter(this::matches)
                                                          .iterator();

        while (iterator.hasNext())
        {
            consumer.accept(iterator.next());
        }
    }

    private boolean matches(Workflow.Lifecycle lifecycle)
    {
        return lifecycle.getCommands()
                        .stream()
                        .anyMatch(c -> command.equals(c) || WILDCARD.equals(c));
    }

    private interface Consumer
    {
        void accept(Workflow.Lifecycle lifecycle) throws Exception;
    }
}
