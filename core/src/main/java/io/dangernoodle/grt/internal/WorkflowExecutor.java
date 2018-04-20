package io.dangernoodle.grt.internal;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;

import io.dangernoodle.grt.Repository;
import io.dangernoodle.grt.Workflow;


public class WorkflowExecutor
{
    private final Map<String, Workflow> workflows;

    WorkflowExecutor(Collection<Workflow> workflows)
    {
        this.workflows = workflows.stream()
                                  .collect(Collectors.toMap(workflow -> workflow.getName(), Function.identity()));
    }

    public void execute(Repository repository) throws Exception
    {
        Workflow.Context context = new Workflow.Context();

        for (String step : repository.getWorkflow())
        {
            if (!workflows.containsKey(step))
            {
                System.out.println("not found");
                continue;
            }

            workflows.get(step).execute(repository, context);
        }
    }

    @ApplicationScoped
    public static class WorkflowExecutorProducer
    {
        @Produces
        @ApplicationScoped
        public WorkflowExecutor get(Instance<Workflow> instance)
        {
            return new WorkflowExecutor(instance.stream().collect(Collectors.toList()));
        }
    }
}
