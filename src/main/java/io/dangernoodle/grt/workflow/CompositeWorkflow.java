package io.dangernoodle.grt.workflow;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.dangernoodle.grt.Workflow;


/**
 * Executes a collection of <code>Workflow</code> objects, optionally ignoring errors.
 * <p>
 * The workflows comprising the collection will have their <code>preExecution</code> and <code>postExecution</code>
 * methods invoked in a loop the object's corresponding method. If you are not interested in this behavior, use the
 * {@link ChainedWorkflow} instead.
 * </p>
 *
 * @param <T> workflow object
 * @since 0.9.0
 */
public class CompositeWorkflow<T> implements Workflow<T>
{
    private static final Logger logger = LoggerFactory.getLogger(CompositeWorkflow.class);

    private final boolean ignoreErrors;

    private final Collection<Workflow<T>> workflows;
    
    public CompositeWorkflow(boolean ignoreErrors, Collection<Workflow<T>> workflows)
    {
        this.workflows = workflows;
        this.ignoreErrors = ignoreErrors;
    }

    @Override
    public void execute(T object, Context context) throws Exception
    {
        for (Workflow<T> workflow : workflows)
        {
            logger.trace("executing workflow [{}] for [{}]", workflow.getName(), object);

            try
            {
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
        }
    }

    @Override
    public void postExecution()
    {
        workflows.forEach(workflow -> {
            logger.trace("executing 'postExecution' for workflow [{}]", workflow.getName());
            workflow.postExecution();
        });
    }

    @Override
    public void preExecution() throws Exception
    {
        for (Workflow<T> workflow : workflows)
        {
            logger.trace("executing 'preExecution' for workflow [{}]", workflow.getName());
            workflow.preExecution();
        }
    }
}
