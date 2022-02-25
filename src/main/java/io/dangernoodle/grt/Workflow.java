package io.dangernoodle.grt;

import static io.dangernoodle.grt.Constants.WILDCARD;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.kohsuke.github.GHRepository;


/**
 * @since 0.9.0
 */
public interface Workflow<T>
{
    void execute(T object, Context context) throws Exception;

    default String getName()
    {
        return getClass().getName();
    }

    default void postExecution()
    {
        // no-op
    }

    default void preExecution()
    {
        // no-op
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
     * @since 0.9.0
     */
    public interface Lifecycle
    {
        default void postExecution()
        {
            // no-op
        }

        default void preExecution()
        {
            // no-op
        }

        default Collection<String> getCommands()
        {
            return List.of(WILDCARD);
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

    public interface Step<T>
    {
        Workflow.Status execute(T object, Context context) throws Exception;
    }
}
