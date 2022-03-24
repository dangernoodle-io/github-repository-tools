package io.dangernoodle.grt.cli.options;

import static io.dangernoodle.grt.Constants.SHA1;
import static io.dangernoodle.grt.Constants.SHA1_OPT;
import static io.dangernoodle.grt.Constants.TAG;
import static io.dangernoodle.grt.Constants.TAG_OPT;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import picocli.CommandLine.Option;
import picocli.CommandLine.ParameterException;


/**
 * Represents a <code>sha1</code> and <code>tag</code> as mutually exclusive options.
 * 
 * <pre>
 * &#64;ArgGroup(exclusive = true, multiplicity = "1")
 * private Sha1OrTagOption sha1OrTag;
 * </pre>
 * 
 * @since 0.9.0
 */
public class Sha1OrTagOption extends CommandOption
{
    private Map<Object, Object> args = new HashMap<>();

    @Option(names = SHA1_OPT, descriptionKey = SHA1)
    public void setSha1(String sha1) throws ParameterException
    {
        args.put(SHA1, Optional.of(sha1)
                               .filter(s -> s.length() > 11)
                               .orElseThrow(() -> createParameterException("sha1 length must be >= 12")));
    }

    @Option(names = TAG_OPT, descriptionKey = TAG)
    public void setTag(String tag)
    {
        args.put(TAG, tag);
    }

    @Override
    public Map<Object, Object> toArgMap()
    {
        // will only contain one of the two args
        return args;
    }
}
