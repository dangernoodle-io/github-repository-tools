package io.dangernoodle.grt.cli;

import static java.nio.file.Files.walkFileTree;

import java.nio.file.Path;

import com.beust.jcommander.Parameters;

import io.dangernoodle.grt.internal.ValidatingFileVisitor;
import io.dangernoodle.grt.utils.JsonTransformer;
import io.dangernoodle.grt.utils.RepositoryFactory;


@Parameters(commandNames = "validate", resourceBundle = "GithubRepositoryTools", commandDescriptionKey = "validate")
public class ValidateCommand implements CommandLineParser.Command
{
    @Override
    public Class<? extends CommandLineExecutor> getCommandExectorClass()
    {
        return ValidatorExecutor.class;
    }

    public static class ValidatorExecutor extends CommandLineExecutor
    {
        private final JsonTransformer transformer;

        private final Path root;

        public ValidatorExecutor(JsonTransformer transformer, Path root)
        {
            this.root = root;
            this.transformer = transformer;
        }

        @Override
        public void execute() throws Exception
        {
            Path definitions = RepositoryFactory.resolveDefinitionsRoot(root);
            ValidatingFileVisitor visitor = new ValidatingFileVisitor(transformer);

            walkFileTree(definitions, visitor);

            boolean hasErrors = visitor.hasErrors();

            if (hasErrors)
            {
                visitor.report();
                throw new IllegalStateException();
            }
        }
    }
}
