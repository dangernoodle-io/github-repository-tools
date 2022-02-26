package io.dangernoodle.grt.util;

import static java.nio.file.Files.walkFileTree;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;

import io.dangernoodle.grt.Command;
import io.dangernoodle.grt.Workflow;
import io.dangernoodle.grt.cli.exector.ValidateExecutor;
import io.dangernoodle.grt.workflow.PathToRepositoryWorkflow;


public abstract class DefinitionExecutor extends Command.Executor
{
    private final Path root;

    private final Workflow<Path> workflow;

    public DefinitionExecutor(Path definitionRoot, Workflow<Path> workflow)
    {
        this.root = definitionRoot;
        this.workflow = workflow;
    }

    protected String getDefinition(Command command)
    {
        return ((Command.Definition) command).getDefinition();
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

    protected Map<String, Object> getArguments(Command command)
    {
        return Collections.emptyMap();
    }

    private Workflow.Context createContext(Command command)
    {
        return new Workflow.Context(getArguments(command));
    }

    private void pathHandler(Path path, Command command)
    {
        logger.info("** definition file [{}]", path);

        try
        {
            workflow.execute(path, createContext(command));
        }
        catch (Exception e)
        {
            // ffs...
            throw new IllegalCallerException(e);
        }
    }

    public static abstract class Repository extends DefinitionExecutor
    {
        private final ValidateExecutor validator;

        public Repository(Path definitionDir, ValidateExecutor validator, PathToRepositoryWorkflow workflow)
        {
            super(definitionDir, workflow);
            this.validator = validator;
        }

        @Override
        public void execute(Command command) throws Exception
        {
            // this is fine for now as the validator doesn't have options
            // validator.execute(null);

            super.execute(command);
        }
    }
}
