package io.dangernoodle.grt.cli.executor;

import java.nio.file.Path;

import io.dangernoodle.grt.workflow.ValidationWorkflow;


public class ValidationExecutor extends DefinitionExecutor
{
    public ValidationExecutor(Path definitionRoot, ValidationWorkflow workflow)
    {
        super(definitionRoot, workflow);
    }
}
