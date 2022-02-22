package io.dangernoodle.grt.cli;

import static java.nio.file.Files.walkFileTree;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.dangernoodle.grt.Repository;
import io.dangernoodle.grt.Workflow;
import io.dangernoodle.grt.internal.RepositoryFileVisitor;
import io.dangernoodle.grt.utils.RepositoryFactory;


public abstract class CommandLineExecutor
{
    protected final Logger logger;

    public CommandLineExecutor()
    {
        this.logger = LoggerFactory.getLogger(getClass());
    }

    public abstract void execute() throws Exception;

    /**
     * @since 0.8.0
     */
    public static abstract class RepositoryExecutor extends CommandLineExecutor
    {
        protected final RepositoryFactory factory;

        protected final Workflow workflow;

        public RepositoryExecutor(RepositoryFactory factory, Workflow workflow)
        {
            this.factory = factory;
            this.workflow = workflow;
        }

        @Override
        public void execute() throws Exception
        {
            RepositoryFileVisitor visitor = new RepositoryFileVisitor(getRepositoryName(), path -> {
                logger.info("** repository file [{}]", path);

                Repository repository = factory.load(path);
                
                try
                {
                    workflow.execute(repository, createContext());
                }
                catch (Exception e)
                {
                    // ffs...
                    throw new IOException(e);
                }
            });

            try
            {
                workflow.preExecution();

                walkFileTree(factory.getDefinitionsRoot(), visitor);

                if (!visitor.matched())
                {
                    throw new FileNotFoundException("failed to find repository file [" + getRepositoryName() + "]");
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                workflow.postExecution();
            }
        }

        /**
         * @since 0.9.0
         */
        protected Map<String, Object> getArguments()
        {
            return Collections.emptyMap();
        }

        protected abstract String getRepositoryName();

        /**
         * @since 0.6.0
         */
        protected boolean isIgnoreErrors()
        {
            return false;
        }

        private Workflow.Context createContext()
        {
            return new Workflow.Context(getArguments());
        }
    }
}
