package io.dangernoodle.grt;

import java.nio.file.Path;


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
}
