package io.dangernoodle.grt;

import java.nio.file.Path;

import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.ParseResult;


/**
 * @since 0.9.0
 */
public interface Arguments
{
    static final String IGNORE_ERRORS = "--ignoreErrors";

    static final String ROOT_DIR = "--rootDir";

    static final String SILENT = "--slient";

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

    boolean isSilent();

    public static class ArgumentsBuilder implements Arguments
    {
        private ParseResult command;

        private ParseResult subcommand;

        @Override
        public String getCommand()
        {
            return subcommand.commandSpec()
                             .name();
        }

        @Override
        public Path getRoot()
        {
            return Path.of((String) command.matchedOption(ROOT_DIR)
                                           .getValue());
        }

        @Override
        public boolean ignoreErrors()
        {
            return subcommand.matchedOptionValue(IGNORE_ERRORS, false);
        }

        public void initialize(ParseResult parseResult)
        {
            this.command = parseResult;
            this.subcommand = getSubcommand(parseResult);
        }

        @Override
        public boolean isSilent()
        {
            return subcommand.matchedOptionValue(SILENT, false);
        }

        private ParseResult getSubcommand(ParseResult parseResult)
        {
            return parseResult.hasSubcommand() ? parseResult.subcommand() : ParseResult.builder(CommandSpec.create()).build();
        }
    }
}
