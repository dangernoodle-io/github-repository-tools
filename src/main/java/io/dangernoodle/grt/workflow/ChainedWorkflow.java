package io.dangernoodle.grt.workflow;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.dangernoodle.grt.Workflow;


public class ChainedWorkflow<T> implements Workflow<T>
{
    private static final Logger logger = LoggerFactory.getLogger(ChainedWorkflow.class);

    private final boolean ignoreErrors;

    private final Collection<Workflow<T>> workflows;

    public ChainedWorkflow(Collection<Workflow<T>> workflows, boolean ignoreErrors)
    {
        this.ignoreErrors = ignoreErrors;
        this.workflows = workflows;
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

                LoggerFactory.getLogger(getClass()).warn("ignoring error:", e);
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
