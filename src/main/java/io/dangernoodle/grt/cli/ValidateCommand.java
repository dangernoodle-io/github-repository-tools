package io.dangernoodle.grt.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.inject.Inject;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import io.dangernoodle.grt.Arguments;
import io.dangernoodle.grt.cli.CommandLineExecutor.RepositoryExecutor;
import io.dangernoodle.grt.json.JsonSchemaValidator;
import io.dangernoodle.grt.json.JsonValidationException;


@Parameters(commandNames = "validate", resourceBundle = "GithubRepositoryTools", commandDescriptionKey = "validate")
public class ValidateCommand implements CommandLineParser.Command
{
    @Parameter(descriptionKey = "repoName", required = true)
    private static String name;

    @Override
    public Class<? extends RepositoryExecutor> getCommandExectorClass()
    {
        return Executor.class;
    }

    public static class Executor extends RepositoryExecutor
    {
        private final JsonSchemaValidator validator;

        @Inject
        public Executor(Arguments arguments, JsonSchemaValidator validator)
        {
            super(arguments);
            this.validator = validator;
        }

        @Override
        protected void execute(File defaults, File overrides) throws Exception
        {
            validate(defaults);
            validate(overrides);
        }

        @Override
        protected String getRepositoryName()
        {
            return name;
        }

        private void validate(File toValidate) throws IOException
        {
            String name = toValidate.getName();

            try
            {
                logger.info("validating [{}]", name);
                validator.validate(() -> new FileInputStream(toValidate));
            }
            catch (@SuppressWarnings("unused") JsonValidationException e)
            {
                logger.error("validation for [{}] failed", name);
            }
        }
    }
}
