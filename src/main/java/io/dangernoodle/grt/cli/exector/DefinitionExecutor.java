package io.dangernoodle.grt.cli.exector;

import static java.nio.file.Files.walkFileTree;

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
        String definition = getDefinition(command);
        DefinitionFileVisitor visitor = new DefinitionFileVisitor(definition, path -> pathHandler(path, command));

        try
        {
            workflow.preExecution();

            walkFileTree(root, visitor);

            if (!visitor.matched())
            {
                throw new FileNotFoundException("failed to find definition file for repository [" + definition + "]");
            }
        }
        catch (Exception e)
        {
            throw e instanceof IllegalCallerException ? (Exception) e.getCause() : e;
        }
        finally
        {
            workflow.postExecution();
        }
    }

    protected String getDefinition(Command command)
    {
        return ((Command.Definition) command).getDefinition();
    }

    private void pathHandler(Path path, Command command)
    {
        logger.debug("definition file [{}]", path);

        try
        {
            workflow.execute(path, new Workflow.Context(command.toArgMap()));
        }
        catch (Exception e)
        {
            // ffs...
            throw new IllegalCallerException(e);
        }
    }
}
