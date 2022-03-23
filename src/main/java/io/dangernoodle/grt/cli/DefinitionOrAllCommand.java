package io.dangernoodle.grt.cli;

import static io.dangernoodle.grt.Constants.ALL_OPT;
import static io.dangernoodle.grt.Constants.FILTER_OPT;
import static io.dangernoodle.grt.Constants.WILDCARD;

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
public class DefinitionOrAllCommand extends Command
{
    @ArgGroup(exclusive = true, multiplicity = "1")
    private DefOrAll defOrAll;

    protected DefinitionOrAllCommand(Injector injector)
    {
        super(injector);
    }

    @Override
    public String getDefinition()
    {
        return Optional.ofNullable(defOrAll.definition)
                       .orElseGet(() -> Optional.ofNullable(defOrAll.all.filter)
                                                .map(filter -> filter + "/" + WILDCARD)
                                                .orElse(WILDCARD));
    }

    private static class DefOrAll
    {
        @Parameters(index = "0")
        private String definition;

        @ArgGroup(exclusive = false)
        private All all;

        private static class All
        {
            @Option(names = ALL_OPT)
            private boolean enabled;

            @Option(names = FILTER_OPT, required = false)
            private String filter;
        }
    }
}
