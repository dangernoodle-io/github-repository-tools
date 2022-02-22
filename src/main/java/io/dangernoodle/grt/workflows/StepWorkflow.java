package io.dangernoodle.grt.workflows;

import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.dangernoodle.grt.Repository;
import io.dangernoodle.grt.Workflow;


public class StepWorkflow implements Workflow
{
    private final Logger logger = LoggerFactory.getLogger(StepWorkflow.class);

    private final Collection<Workflow.Step> steps;

    public StepWorkflow(Collection<Workflow.Step> steps)
    {
        this.steps = steps;
    }

    @Override
    public void execute(Repository repository, Context context) throws Exception
    {
        for (Workflow.Step step : steps)
        {
            String stepName = step.getClass().getSimpleName();
            logger.trace("executing workflow [{}] step [{}]", getName(), step);

            Status status = step.execute(repository, context);

            if (status != Status.CONTINUE)
            {
                logger.debug("workflow [{}] aborted by step [{}]", getName(), stepName);
                return;
            }
        }
    }

    public int getStepCount()
    {
        return steps.size();
    }

    public List<Workflow.Step> getSteps()
    {
        return List.copyOf(steps);
    }
}
