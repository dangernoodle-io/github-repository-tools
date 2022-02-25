package io.dangernoodle.grt.cli.executor;

import java.nio.file.Path;

import io.dangernoodle.grt.Command;
import io.dangernoodle.grt.workflows.ValidatorWorkflow;


public class ValidateExecutor extends DefinitionExecutor
{
    public ValidateExecutor(Path definitionRoot, ValidatorWorkflow workflow)
    {
        super(definitionRoot, workflow);
    }

    @Override
    protected String getDefinition(Command command)
    {
        return "*";
    }
}
