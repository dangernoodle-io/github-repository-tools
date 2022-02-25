package io.dangernoodle.grt;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Callable;

import com.google.inject.Injector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @since 0.9.0
 */
public abstract class Command implements Callable<Void>
{
    private final Injector injector;

    public Command(Injector injector)
    {
        this.injector = injector;
    }

    public Map<String, Object> asMap()
    {
        return Collections.emptyMap();
    }

    @Override
    public Void call() throws Exception
    {
        injector.getInstance(getExecutor())
                .execute(this);

        return null;
    }

    public boolean ignoreErrors()
    {
        return false;
    }

    protected abstract Class<? extends Command.Executor> getExecutor();

    /**
     * @since 0.9.0
     */
    public static abstract class Definition extends Command
    {
 
        public Definition(Injector injector)
        {
            super(injector);
        }
        
        public abstract String getDefinition();
    }

    /**
     * @since 0.9.0
     */
    public static abstract class Executor
    {
        protected final Logger logger;

        public Executor()
        {
            this.logger = LoggerFactory.getLogger(getClass());
        }

        public abstract void execute(Command command) throws Exception;
    }
}
