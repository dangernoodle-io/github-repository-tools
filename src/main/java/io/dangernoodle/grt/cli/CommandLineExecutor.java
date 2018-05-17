package io.dangernoodle.grt.cli;

import java.io.File;

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
            File overrides = loader.loadRepository(getRepositoryName());

            execute(defaults, overrides);
        }

        protected abstract void execute(File defaults, File overrides) throws Exception;

        protected abstract String getRepositoryName();
    }
}
