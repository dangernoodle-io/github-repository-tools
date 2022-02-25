package io.dangernoodle.grt.workflow;

import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.dangernoodle.grt.Workflow;


public class StepWorkflow<T> implements Workflow<T>
{
    private final Logger logger = LoggerFactory.getLogger(StepWorkflow.class);

    private final String name;

    private final Collection<Workflow.Step<T>> steps;
    
    public StepWorkflow(String name, Collection<Workflow.Step<T>> steps)
    {
        this.name = name;
        this.steps = steps;
    }

    @Override
    public void execute(T object, Context context) throws Exception
    {
        for (Workflow.Step<T> step : steps)
        {
            String stepName = step.getClass().getSimpleName();
            logger.trace("executing workflow [{}] step [{}]", getName(), step);

            Status status = step.execute(object, context);

            if (status != Status.CONTINUE)
            {
                logger.debug("workflow [{}] aborted by step [{}]", getName(), stepName);
                return;
            }
        }
    }

    @Override
    public String getName()
    {
        return name;
    }

    public int getStepCount()
    {
        return steps.size();
    }

    public List<Workflow.Step<T>> getSteps()
    {
        return List.copyOf(steps);
    }
}
