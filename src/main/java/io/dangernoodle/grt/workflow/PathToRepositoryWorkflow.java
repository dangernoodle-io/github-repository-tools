package io.dangernoodle.grt.workflow;

import java.nio.file.Path;

import io.dangernoodle.grt.Repository;
import io.dangernoodle.grt.Workflow;
import io.dangernoodle.grt.utils.RepositoryFactory;


public class PathToRepositoryWorkflow implements Workflow<Path>
{
    private final RepositoryFactory factory;

    private final Workflow<Repository> workflow;

    public PathToRepositoryWorkflow(RepositoryFactory factory, Workflow<Repository> workflow)
    {
        this.factory = factory;
        this.workflow = workflow;
    }

    @Override
    public void execute(Path object, Context context) throws Exception
    {
        workflow.execute(factory.load(object), context);
    }

    @Override
    public void postExecution()
    {
        workflow.postExecution();
    }

    @Override
    public void preExecution()
    {
        workflow.preExecution();
    }
}
