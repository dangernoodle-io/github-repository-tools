package io.dangernoodle.grt.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.dangernoodle.grt.Repository;
import io.dangernoodle.grt.Workflow;


public class WorkflowExecutor
{
    private static final Logger logger = LoggerFactory.getLogger(WorkflowExecutor.class);

    private final Collection<Workflow.PrePost> prePost;

    private final Map<String, Workflow> workflows;

    public WorkflowExecutor(Collection<Workflow> workflows, Collection<Workflow.PrePost> prePost)
    {
        this.prePost = prePost;
        this.workflows = workflows.stream()
                                  .collect(Collectors.toMap(workflow -> workflow.getName(), Function.identity()));
    }

    public void execute(Repository repository, Map<String, Object> args) throws Exception
    {
        Workflow.Context context = new Workflow.Context(args);
        Collection<String> steps = getSteps(repository.getWorkflow());

        for (String step : steps)
        {
            if (!workflows.containsKey(step))
            {
                logger.warn("unable to find Workflow instance for step [{}]", step);
                continue;
            }

            workflows.get(step)
                     .execute(repository, context);
        }
    }

    public void postExecution() throws Exception
    {
        for (Workflow.PrePost toExecute : prePost)
        {
            toExecute.postExecution();
        }
    }

    public void preExecution() throws Exception
    {
        for (Workflow.PrePost toExecute : prePost)
        {
            toExecute.preExecution();
        }
    }

    private Collection<String> getSteps(Collection<String> workflow)
    {
        ArrayList<String> steps = new ArrayList<>();

        if (workflow != null)
        {
            steps.addAll(workflow);
        }

        if (workflow == null || !steps.contains("github"))
        {
            steps.add(0, "github");
        }

        return steps;
    }
}
