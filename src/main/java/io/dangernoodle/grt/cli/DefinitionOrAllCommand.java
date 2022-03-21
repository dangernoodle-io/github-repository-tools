package io.dangernoodle.grt.cli;

import static io.dangernoodle.grt.Constants.*;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import com.google.inject.Injector;

import io.dangernoodle.grt.Command;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;


/**
 * Base class for commands that work against one or all definition files.
 * 
 * @since 0.9.0
 */
public abstract class DefinitionOrAllCommand extends Command
{
    @ArgGroup(exclusive = true, multiplicity = "1")
    private DefOrAll defOrAll;

    public DefinitionOrAllCommand(Injector injector)
    {
        super(injector);
    }

    @Override
    public String getDefinition()
    {
        String definition = defOrAll.definition;

        return Optional.ofNullable(defOrAll.mutual.filter)
                       .filter(filter -> WILDCARD.equals(definition))
                       .map(filter -> filter + "/" + WILDCARD)
                       .orElse(definition);
    }

    @Override
    public Map<Object, Object> toArgMap()
    {
        return Collections.emptyMap();
    }

    private static class DefOrAll
    {
        @Parameters(index = "0")
        private String definition;

        @ArgGroup
        private Mutual mutual;

        private class Mutual
        {
            @Option(names = ALL_OPT)
            private boolean all;

            @Option(names = FILTER_OPT, required = false)
            private String filter;
        }
    }
}
