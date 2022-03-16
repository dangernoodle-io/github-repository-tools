package io.dangernoodle.grt.workflow;

import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.dangernoodle.grt.Workflow;


/**
 * Executes a collection of <code>Workflow</code> objects, optionally ignoring errors.
 * <p>
 * The workflows comprising the collection will have their <code>preExecution</code> and <code>postExecution</code>
 * methods invoked as part of this object's <code>execute</code> method. If you are not interested in this behavior, use
 * the {@link ChainedWorkflow} instead.
 * </p>
 *
 * @param <T> workflow object
 * @since 0.9.0
 */
public class ChainedWorkflow<T> implements Workflow<T>
{
    private static final Logger logger = LoggerFactory.getLogger(ChainedWorkflow.class);

    private final boolean ignoreErrors;

    private final Collection<Workflow<T>> workflows;

    public ChainedWorkflow(boolean ignoreErrors, Collection<Workflow<T>> workflows)
    {
        this.workflows = workflows;
        this.ignoreErrors = ignoreErrors;
    }

    @SafeVarargs
    public ChainedWorkflow(boolean ignoreErrors, Workflow<T>... workflows)
    {
        this(ignoreErrors, List.of(workflows));
    }

    @Override
    public void execute(T object, Context context) throws Exception
    {
        for (Workflow<T> workflow : workflows)
        {
            logger.trace("executing workflow [{}] for [{}]", workflow.getName(), object);
            execute(workflow, object, context);
        }
    }

    private void execute(Workflow<T> workflow, T object, Context context) throws Exception
    {
        try
        {
            // abort execution as well if we don't make it past here
            workflow.preExecution();
            workflow.execute(object, context);
        }
        catch (Exception e)
        {
            if (!ignoreErrors)
            {
                throw e;
            }

            logger.warn("ignoring error:", e);
        }
        finally
        {
            workflow.postExecution();
        }
    }
}
