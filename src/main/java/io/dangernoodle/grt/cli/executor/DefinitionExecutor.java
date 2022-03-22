package io.dangernoodle.grt.cli.executor;

import java.io.FileNotFoundException;
import java.nio.file.Path;

import io.dangernoodle.grt.Command;
import io.dangernoodle.grt.Workflow;
import io.dangernoodle.grt.util.DefinitionFileVisitor;


public class DefinitionExecutor extends CommandExecutor
{
    private final Path root;

    private final Workflow<Path> workflow;

    public DefinitionExecutor(Path definitionRoot, Workflow<Path> workflow)
    {
        this.root = definitionRoot;
        this.workflow = workflow;
    }

    @Override
    public void execute(Command command) throws Exception
    {
        String definition = command.getDefinition();

        try
        {
            workflow.preExecution();

            int count = visitor(definition).visit(root, path -> executeWorkflow(path, command));

            if (count == 0)
            {
                throw new FileNotFoundException("failed to find definition file for repository [" + definition + "]");
            }
        }
        finally
        {
            workflow.postExecution();
        }
    }

    void executeWorkflow(Path path, Command command) throws Exception
    {
        logger.debug("definition file [{}]", path);
        workflow.execute(path, new Workflow.Context(command.toArgMap()));
    }

    // visible for
    DefinitionFileVisitor visitor(String definition)
    {
        return new DefinitionFileVisitor(definition);
    }
}
