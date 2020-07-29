package io.dangernoodle.grt.cli;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import io.dangernoodle.grt.Arguments;
import io.dangernoodle.grt.cli.CommandLineExecutor.RepositoryExecutor;
import io.dangernoodle.grt.utils.JsonTransformer;
import io.dangernoodle.grt.utils.JsonValidationException;


@Parameters(commandNames = "validate", resourceBundle = "GithubRepositoryTools", commandDescriptionKey = "validate")
public class ValidateCommand implements CommandLineParser.Command
{
    @Parameter(descriptionKey = "name", required = true)
    private static String name;

    @Override
    public Class<? extends RepositoryExecutor> getCommandExectorClass()
    {
        return Executor.class;
    }

    public static class Executor extends RepositoryExecutor
    {
        @Inject
        public Executor(Arguments arguments, JsonTransformer transformer)
        {
            super(arguments, transformer);
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
                transformer.validate(toValidate);
            }
            catch (@SuppressWarnings("unused") JsonValidationException e)
            {
                logger.error("validation for [{}] failed", name);
            }
        }
    }
}
