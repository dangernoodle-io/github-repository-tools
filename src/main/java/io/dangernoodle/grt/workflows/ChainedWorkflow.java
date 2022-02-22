package io.dangernoodle.grt.workflows;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.dangernoodle.grt.Repository;
import io.dangernoodle.grt.Workflow;


public class ChainedWorkflow implements Workflow
{
    private static final Logger logger = LoggerFactory.getLogger(ChainedWorkflow.class);

    private final Collection<Workflow> workflows;

    private final boolean ignoreErrors;

    public ChainedWorkflow(Collection<Workflow> workflows, boolean ignoreErrors)
    {
        this.ignoreErrors = ignoreErrors;
        this.workflows = workflows;
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
    public void preExecution()
    {
        workflows.forEach(workflow -> {
            logger.trace("executing 'preExecution' for workflow [{}]", workflow.getName());
            workflow.preExecution();
        });
    }

    @Override
    public void execute(Repository repository, Context context) throws Exception
    {
        for (Workflow workflow : workflows)
        {
            logger.trace("executing workflow [{}] for repository [{}]", workflow.getName(), repository.getName());

            try
            {
                workflow.execute(repository, context);
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
}
