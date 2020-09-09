package io.dangernoodle.grt;

import java.util.HashMap;
import java.util.Map;

import org.kohsuke.github.GHRepository;


public interface Workflow
{
    void execute(Repository project, Context context) throws Exception;

    String getName();

    public class Context
    {
        private Map<Class<?>, Object> map = new HashMap<>();

        public void add(Object object)
        {
            map.put(object.getClass(), object);
        }

        @SuppressWarnings("unchecked")
        public <T> T get(Class<T> clazz)
        {
            return (T) map.get(clazz);
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
        default void preExecution() throws Exception
        {
            // no-op
        }

        default void postExecution() throws Exception
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
