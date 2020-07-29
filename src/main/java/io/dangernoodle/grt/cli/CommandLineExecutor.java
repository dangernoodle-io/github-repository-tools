package io.dangernoodle.grt.cli;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.dangernoodle.grt.Arguments;
import io.dangernoodle.grt.internal.FileLoader;
import io.dangernoodle.grt.utils.JsonTransformer;


public abstract class CommandLineExecutor
{
    protected final FileLoader loader;

    protected final Logger logger;

    protected final JsonTransformer transformer;

    public CommandLineExecutor(Arguments arguments, JsonTransformer transformer)
    {
        this.transformer = transformer;
        this.loader = new FileLoader(arguments.getRepoDir());

        this.logger = LoggerFactory.getLogger(getClass());
    }

    public abstract void execute() throws Exception;

    public static abstract class RepositoryExecutor extends CommandLineExecutor
    {
        public RepositoryExecutor(Arguments arguments, JsonTransformer transformer)
        {
            super(arguments, transformer);
        }

        @Override
        public void execute() throws Exception
        {
            File defaults = loader.loadRepositoryDefaults();
            Collection<File> repositories = getRepositories();

            repositories.forEach(repo -> {
                try
                {
                    logger.info("** processing repository file [{}]", repo);
                    execute(defaults, repo);
                }
                catch (Exception e)
                {
                    logger.error("", e);
                }
            });
        }

        protected abstract void execute(File defaults, File overrides) throws Exception;

        protected abstract String getRepositoryName();

        protected Collection<File> getRepositories() throws IOException
        {
            return Arrays.asList(loader.loadRepository(getRepositoryName()));
        }
    }
}
