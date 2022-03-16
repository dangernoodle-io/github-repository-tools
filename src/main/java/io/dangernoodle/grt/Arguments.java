package io.dangernoodle.grt;

import static io.dangernoodle.grt.Constants.ALL_OPT;
import static io.dangernoodle.grt.Constants.IGNORE_ERRORS_OPT;
import static io.dangernoodle.grt.Constants.ROOT_DIR_OPT;

import java.nio.file.Path;
import java.util.Optional;

import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.ParseResult;


/**
 * @since 0.9.0
 */
public interface Arguments
{
    default boolean enabledForAll()
    {
        return getOption(ALL_OPT, false);
    }

    String getCommand();

    default Path getConfigurationPath()
    {
        return getRoot().resolve("github-repository-tools.json");
    }

    default Path getCredentialsPath()
    {
        return getRoot().resolve("credentials.json");
    }

    default Path getDefinitionsRootPath()
    {
        return getRoot().resolve("repositories");
    }

    default <T> T getOption(String name)
    {
        return getOption(name, null);
    }

    <T> T getOption(String name, T dflt);

    default Path getRoot()
    {
        return getOption(ROOT_DIR_OPT);
    }

    boolean hasOption(String name);

    default boolean ignoreErrors()
    {
        return getOption(IGNORE_ERRORS_OPT, false);
    }

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
        public <T> T getOption(String name, T dflt)
        {
            return Optional.ofNullable(subcommand.matchedOptionValue(name, dflt))
                           .or(() -> Optional.ofNullable(command.matchedOptionValue(name, dflt)))
                           .orElse(null);
        }

        @Override
        public boolean hasOption(String name)
        {
            return command.hasMatchedOption(name) || subcommand.hasMatchedOption(name);
        }

        public void initialize(ParseResult parseResult)
        {
            this.command = parseResult;
            this.subcommand = getSubcommand(parseResult);
        }

        private ParseResult getSubcommand(ParseResult parseResult)
        {
            return parseResult.hasSubcommand() ? parseResult.subcommand() : ParseResult.builder(CommandSpec.create()).build();
        }
    }
}
