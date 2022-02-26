package io.dangernoodle.grt.cli.exector;

import java.nio.file.Path;
import java.util.Map;

import io.dangernoodle.grt.Command;
import io.dangernoodle.grt.Workflow;
import io.dangernoodle.grt.util.DefinitionExecutor;


public class RepositoryExecutor extends DefinitionExecutor
{
    public RepositoryExecutor(Path definitionRoot, Workflow<Path> workflow)
    {
        super(definitionRoot, workflow);
    }

    @Override
    protected Map<String, Object> getArguments(Command command)
    {
        // TODO Auto-generated method stub
        return super.getArguments(command);
    }
}
