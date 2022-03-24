package io.dangernoodle.grt.cli.options;

import static io.dangernoodle.grt.Constants.REF;
import static io.dangernoodle.grt.Constants.REF_OPT;

import java.util.Map;
import java.util.Optional;

import picocli.CommandLine.Option;


/**
 * Represents a git <code>reference</code>, eg: <code>refs/heads/foo</code>
 * 
 * <pre>
 * &#64;Mixin
 * private ReferenceOption ref;
 * </pre>
 * 
 * @since 0.9.0
 */
public class ReferenceOption extends CommandOption
{
    private String ref;

    @Option(names = REF_OPT, descriptionKey = REF, required = true)
    public void setRef(String ref)
    {
        this.ref = Optional.of(ref)
                           .filter(s -> s.startsWith("refs/"))
                           .orElseThrow(() -> createParameterException("'ref' must start with 'refs/'"));
    }

    @Override
    public Map<Object, Object> toArgMap()
    {
        return Map.of(REF, ref);
    }
}
