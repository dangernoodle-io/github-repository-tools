package io.dangernoodle.grt.internal;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;

import io.dangernoodle.grt.Arguments;
import io.dangernoodle.grt.Repository;
import io.dangernoodle.grt.Workflow;


public class WorkflowExecutor
{
    private final String root;

    private final Map<String, Workflow> workflows;

    WorkflowExecutor(String root, Collection<Workflow> workflows)
    {
        this.root = root;
        this.workflows = workflows.stream()
                                  .collect(Collectors.toMap(workflow -> workflow.getName(), Function.identity()));
    }

    public void execute(File file) throws Exception
    {
        Repository defaults = Repository.load(new File(root + File.separator + "configuration.json"));
        Repository overrides = Repository.load(file);
        
        RepositoryMerger merger = new RepositoryMerger(defaults, overrides);
        Repository repository = merger.merge();

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
        public WorkflowExecutor get(Instance<Workflow> instance, Arguments arguments)
        {
            return new WorkflowExecutor(arguments.getRoot(), instance.stream().collect(Collectors.toList()));
        }
    }
}
