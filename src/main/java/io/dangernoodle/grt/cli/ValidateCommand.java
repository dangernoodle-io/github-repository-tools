package io.dangernoodle.grt.cli;

import java.io.File;
import java.io.IOException;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import io.dangernoodle.grt.internal.FileLoader;
import io.dangernoodle.grt.utils.JsonTransformer;
import io.dangernoodle.grt.utils.JsonValidationException;


@Parameters(commandNames = "validate", resourceBundle = "GithubRepositoryTools", commandDescriptionKey = "validate")
public class ValidateCommand implements CommandLineParser.Command
{
    @Parameter(descriptionKey = "name", required = true)
    private static String name;

    @Override
    public Class<? extends CommandLineExecutor.RepositoryFileExecutor> getCommandExectorClass()
    {
        return Executor.class;
    }

    public static class Executor extends CommandLineExecutor.RepositoryFileExecutor
    {
        private final JsonTransformer transformer;

        public Executor(FileLoader fileLoader, JsonTransformer transformer)
        {
            super(fileLoader);
            this.transformer = transformer;
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
