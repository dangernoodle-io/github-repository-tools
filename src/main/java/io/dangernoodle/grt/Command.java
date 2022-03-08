package io.dangernoodle.grt;

import static io.dangernoodle.grt.Constants.ENABLE_AUTO_ADD_WORKFLOW;
import static io.dangernoodle.grt.Constants.FILTER_OPT;
import static io.dangernoodle.grt.Constants.WILDCARD;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;

import com.google.inject.Injector;

import io.dangernoodle.grt.cli.exector.CommandExecutor;
import io.dangernoodle.grt.cli.exector.ValidatingExecutor;
import io.dangernoodle.grt.util.CommandArguments;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
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

    public Map<Object, Object> toArgMap()
    {
        return Map.of(ENABLE_AUTO_ADD_WORKFLOW, enableAutoAddWorkflow());
    }

    protected ParameterException createParameterException(String message)
    {
        return CommandArguments.createParameterException(spec, message);
    }

    protected void customValidation() throws ParameterException
    {
        // no-op
    }

    protected boolean enableAutoAddWorkflow()
    {
        return true;
    }

    protected abstract Class<? extends CommandExecutor> getExecutor();

    public interface Definition
    {
        String getDefinition();

        public static abstract class All extends Command implements Definition
        {
            @ArgGroup(exclusive = true, multiplicity = "1")
            private CommandArguments.DefininitionOrAll defOrAll;

            @Option(names = FILTER_OPT, required = false)
            private String filter;

            public All(Injector injector)
            {
                super(injector);
            }

            @Override
            public String getDefinition()
            {
                String definition = defOrAll.getDefintion();

                return Optional.ofNullable(filter)
                               .filter(filter -> WILDCARD.equals(definition))
                               .map(filter -> filter + "/" + WILDCARD)
                               .orElse(definition);
            }

            @Override
            protected Class<? extends CommandExecutor> getExecutor()
            {
                return ValidatingExecutor.class;
            }
        }

        /**
         * @since 0.9.0
         */
        public static abstract class Only extends Command implements Definition
        {
            @Mixin
            private CommandArguments.Definition def;

            public Only(Injector injector)
            {
                super(injector);
            }

            @Override
            public String getDefinition()
            {
                return def.getDefintion();
            }

            @Override
            protected Class<? extends CommandExecutor> getExecutor()
            {
                return ValidatingExecutor.class;
            }
        }
    }
}
