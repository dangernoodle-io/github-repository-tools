package io.dangernoodle.grt;

import static io.dangernoodle.grt.Constants.ENABLE_AUTO_ADD_WORKFLOW;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.inject.Injector;

import io.dangernoodle.grt.cli.executor.CommandExecutor;
import io.dangernoodle.grt.cli.executor.ValidatingExecutor;
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

    @Override
    public Void call() throws Exception
    {
        customValidation();

        injector.getInstance(getExecutor())
                .execute(this);

        return null;
    }

    /**
     * @since 0.10.0
     */
    public boolean disableSchema()
    {
        return false;
    }
    
    public abstract String getDefinition();

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
        return new ParameterException(spec.commandLine(), message);
    }

    protected void customValidation() throws ParameterException
    {
        // no-op
    }

    protected boolean enableAutoAddWorkflow()
    {
        return true;
    }

    protected Class<? extends CommandExecutor> getExecutor()
    {
        return ValidatingExecutor.class;
    }

    @SafeVarargs
    protected final Map<Object, Object> merge(Map<Object, Object>... maps)
    {
        return Stream.of(maps)
                     .map(Map::entrySet)
                     .flatMap(Set::stream)
                     .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
