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

    default void preExecution() throws Exception
    {
        // no-op
    }

    public static class Context
    {
        private final Map<Object, Object> context;

        public Context(Map<Object, Object> initial)
        {
            this.context = new HashMap<>(initial);
        }

        public void add(Object object)
        {
            add(object.getClass(), object);
        }

        public void add(Object name, Object object)
        {
            context.put(name, object);
        }

        public boolean contains(Object object)
        {
            return context.containsKey(object);
        }

        public <T> T get(Class<T> clazz) throws IllegalStateException
        {
            return get((Object) clazz);
        }
        
        public <T> T get(Class<T> clazz, T dflt)
        {
            return get((Object) clazz, dflt);
        }

        @SuppressWarnings("unchecked")
        public <T> T get(Object name) throws IllegalStateException
        {
            return (T) getOptional(name).orElseThrow(() -> illegalState(name));
        }
        
       @SuppressWarnings("unchecked")
        public <T> T get(Object name, T dflt)
        {
            return (T) getOptional(name).orElse(dflt);
        }

        /**
         * @since 0.6.0
         */
        public GHRepository getGHRepository()
        {
            return get(GHRepository.class);
        }

        public <T> Optional<T> getOptional(Class<T> clazz)
        {
            return getOptional((Object) clazz);
        }

        @SuppressWarnings("unchecked")
        public <T> Optional<T> getOptional(Object name)
        {
            return (Optional<T>) Optional.ofNullable(context.get(name));
        }

        private IllegalStateException illegalState(Object name)
        {
            return new IllegalStateException("argument [" + name.toString() + "] not found in context");
        }
    }

    /**
     * @since 0.9.0
     */
    public interface Lifecycle
    {
        default Collection<String> getCommands()
        {
            return List.of(WILDCARD);
        }

        default void postExecution()
        {
            // no-op
        }

        default void preExecution()
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

    public interface Step<T>
    {
        Workflow.Status execute(T object, Context context) throws Exception;
    }
}
