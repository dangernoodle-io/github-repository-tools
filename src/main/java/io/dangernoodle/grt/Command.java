package io.dangernoodle.grt;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Callable;

import com.google.inject.Injector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.dangernoodle.grt.util.CommandArguments;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Spec;


/**
 * @since 0.9.0
 */
public abstract class Command implements Callable<Void>
{
    private final Injector injector;

    @Spec
    private CommandSpec spec;

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
        customValidation();

        injector.getInstance(getExecutor())
                .execute(this);

        return null;
    }

    public boolean ignoreErrors()
    {
        return false;
    }

    protected ParameterException createParameterException(String message)
    {
        return CommandArguments.createParameterException(spec, message);
    }

    protected void customValidation() throws ParameterException
    {
        // no-op
    }

    protected abstract Class<? extends Command.Executor> getExecutor();

    public interface Definition
    {
        String getDefinition();
    }

    /**
     * @since 0.9.0
     */
    public static abstract class DefinitionOnly extends Command implements Definition
    {
        @Mixin
        private CommandArguments.Definition def;

        public DefinitionOnly(Injector injector)
        {
            super(injector);
        }

        @Override
        public String getDefinition()
        {
            return def.getDefintion();
        }
    }

    public static abstract class DefinitionOrAll extends Command implements Definition
    {
        @ArgGroup(exclusive = true, multiplicity = "1")
        private CommandArguments.DefininitionOrAll defOrAll;

        public DefinitionOrAll(Injector injector)
        {
            super(injector);
        }

        @Override
        public String getDefinition()
        {
            return defOrAll.getDefintion();
        }
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
