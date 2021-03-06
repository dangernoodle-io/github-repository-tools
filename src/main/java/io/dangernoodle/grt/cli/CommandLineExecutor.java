package io.dangernoodle.grt.cli;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.dangernoodle.grt.internal.FileLoader;


public abstract class CommandLineExecutor
{
    protected final Logger logger;

    public CommandLineExecutor()
    {
        this.logger = LoggerFactory.getLogger(getClass());
    }

    public abstract void execute() throws Exception;

    public static abstract class RepositoryFileExecutor extends CommandLineExecutor
    {
        protected final FileLoader fileLoader;

        public RepositoryFileExecutor(FileLoader fileLoader)
        {
            this.fileLoader = fileLoader;
        }

        @Override
        public void execute() throws Exception
        {
            File defaults = loadRepositoryDefaults();

            try
            {
                preExecution();

                for (File repo : getRepositories())
                {
                    doExecute(defaults, repo);
                }
            }
            finally
            {
                postExecution();
            }
        }

        protected abstract void execute(File defaults, File overrides) throws Exception;

        protected Collection<File> getRepositories() throws IOException
        {
            return Arrays.asList(fileLoader.loadRepository(getRepositoryName()));
        }

        protected abstract String getRepositoryName();

        /**
         * @since 0.6.0
         */
        protected boolean isIgnoreErrors()
        {
            return false;
        }

        /**
         * @since 0.6.0
         */
        protected void postExecution() throws Exception
        {
            // no-op
        }

        /**
         * @since 0.6.0
         */
        protected void preExecution() throws Exception
        {
            // no-op
        }

        // visible for testing
        File loadRepositoryDefaults() throws IOException
        {
            return fileLoader.loadRepositoryDefaults();
        }

        private void doExecute(File defaults, File repo) throws Exception
        {
            try
            {
                logger.info("** processing repository file [{}]", repo);
                execute(defaults, repo);

            }
            catch (Exception e)
            {
                if (!isIgnoreErrors())
                {
                    throw e;
                }

                logger.error("", e);
            }
        }
    }
}
