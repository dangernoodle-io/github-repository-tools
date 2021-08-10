package io.dangernoodle.grt;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.jboss.weld.exceptions.IllegalStateException;
import org.kohsuke.github.GHRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public interface Workflow
{
    void execute(Repository project, Context context) throws Exception;

    String getName();

    /**
     * @since 0.8.0
     */
    default boolean includeInRepositoryWorkflow()
    {
        return true;
    }

    /**
     * @since 0.8.0
     */
    public abstract static class Basic implements Workflow
    {
        protected final Logger logger;

        protected Basic()
        {
            this.logger = LoggerFactory.getLogger(getClass());
        }

        @Override
        public final void execute(Repository repository, Context context) throws Exception
        {
            Collection<Workflow.Step> steps = createSteps();

            for (Workflow.Step step : steps)
            {
                String stepName = step.getClass().getSimpleName();

                logger.trace("executing step [{}]", stepName);
                Status status = step.execute(repository, context);

                if (status != Status.CONTINUE)
                {
                    logger.debug("[{}] workflow interrupted by [{}], aborting", getName(), stepName);
                    return;
                }
            }
        }

        protected abstract Collection<Workflow.Step> createSteps();
    }

    public static class Context
    {
        private final Map<String, Object> args;

        private final Map<Class<?>, Object> context;

        public Context(Map<String, Object> args)
        {
            this.args = new HashMap<>(args);
            this.context = new HashMap<>();
        }

        public void add(Object object)
        {
            context.put(object.getClass(), object);
        }

        @SuppressWarnings("unchecked")
        public <T> T get(Class<T> clazz)
        {
            return (T) context.get(clazz);
        }

        /**
         * @since 0.8.0
         */
        @SuppressWarnings("unchecked")
        public <T> T getArg(String name)
        {
            return (T) Optional.ofNullable(args.get(name))
                               .orElseThrow(() -> new IllegalStateException(("argument [" + name + "] not found in context")));
        }

        @SuppressWarnings("unchecked")
        public <T> T getArg(String name, T deflt)
        {
            return args.containsKey(name) ? (T) args.get(name) : deflt;
        }

        /**
         * @since 0.6.0
         */
        public GHRepository getGHRepository()
        {
            return get(GHRepository.class);
        }

        /**
         * @since 0.6.0
         */
        public boolean isArchived()
        {
            return getGHRepository().isArchived();
        }
    }

    /**
     * @since 0.6.0
     */
    public interface PrePost
    {
        default void postExecution() throws Exception
        {
            // no-op
        }

        default void preExecution() throws Exception
        {
            // no-op
        }
    }

    /**
     * @since 0.6.0
     */
    public enum Status
    {
        CONTINUE,
        SKIP;
    }

    public interface Step
    {
        Workflow.Status execute(Repository repository, Context context) throws Exception;
    }
}
