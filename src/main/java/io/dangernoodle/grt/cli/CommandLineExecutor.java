package io.dangernoodle.grt.cli;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.dangernoodle.grt.Arguments;
import io.dangernoodle.grt.FileLoader;


public abstract class CommandLineExecutor
{
    protected final Logger logger;

    protected final FileLoader loader;

    public CommandLineExecutor(Arguments arguments)
    {
        this.loader = new FileLoader(arguments.getRepoDir());
        this.logger = LoggerFactory.getLogger(getClass());
    }

    public abstract void execute() throws Exception;

    public static abstract class RepositoryExecutor extends CommandLineExecutor
    {
        public RepositoryExecutor(Arguments arguments)
        {
            super(arguments);
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
