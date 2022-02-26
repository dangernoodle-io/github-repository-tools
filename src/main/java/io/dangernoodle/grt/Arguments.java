package io.dangernoodle.grt;

import java.nio.file.Path;

import picocli.CommandLine.ParseResult;


/**
 * @since 0.9.0
 */
public interface Arguments
{
    public static final String IGNORE_ERRORS = "--ignoreErrors";

    public static final String ROOT_DIR = "--rootDir";

    String getCommand();

    default Path getConfiguration()
    {
        return getRoot().resolve("github-repository-tools.json");
    }

    default Path getCredentials()
    {
        return getRoot().resolve("credentials.json");
    }

    default Path getDefinitionsRoot()
    {
        return getRoot().resolve("repositories");
    }

    Path getRoot();

    boolean ignoreErrors();

    public static class ArgumentsBuilder implements Arguments
    {
        private String command;

        private String rootDir;

        private boolean ignoreErrors;

        @Override
        public String getCommand()
        {
            return command;
        }

        @Override
        public Path getRoot()
        {
            return Path.of(rootDir);
        }

        @Override
        public boolean ignoreErrors()
        {
            return ignoreErrors;
        }

        public void initialize(ParseResult parseResult)
        {
            this.rootDir = parseResult.matchedOption(Arguments.ROOT_DIR)
                                      .getValue();

            if (parseResult.hasSubcommand())
            {
                initFromCommand(parseResult.subcommand());
            }
        }

        private void initFromCommand(ParseResult parseResult)
        {
            if (!parseResult.errors().isEmpty())
            {
                return;
            }

            this.command = parseResult.commandSpec().name();
            this.ignoreErrors = parseResult.matchedOptionValue(Arguments.IGNORE_ERRORS, false);
        }
    }
}
