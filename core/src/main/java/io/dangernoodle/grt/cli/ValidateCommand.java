package io.dangernoodle.grt.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.dangernoodle.grt.Arguments;
import io.dangernoodle.grt.json.JsonSchemaValidator;
import io.dangernoodle.grt.json.JsonValidationException;


@ApplicationScoped
@Parameters(commandNames = "validate", commandDescription = "validate a repository configuration")
public class ValidateCommand implements CommandLineDelegate.Command
{
    private static final Logger logger = LoggerFactory.getLogger(ValidateCommand.class);

    @Parameter(description = "repository name", required = true)
    private static String name;

    @Override
    public Class<? extends Executor> getCommandExectorClass()
    {
        return Executor.class;
    }

    @ApplicationScoped
    public static class Executor extends CommandLineDelegate.RepositoryExecutor
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
                logger.error("validation for [{}] failed", toValidate.getName());
            }
        }
    }
}
