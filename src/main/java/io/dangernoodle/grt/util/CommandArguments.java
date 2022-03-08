package io.dangernoodle.grt.util;

import static io.dangernoodle.grt.Constants.ALL_OPT;
import static io.dangernoodle.grt.Constants.PASSWORD;
import static io.dangernoodle.grt.Constants.PASSWORD_OPT;
import static io.dangernoodle.grt.Constants.REF;
import static io.dangernoodle.grt.Constants.REF_OPT;
import static io.dangernoodle.grt.Constants.SHA1;
import static io.dangernoodle.grt.Constants.SHA1_OPT;
import static io.dangernoodle.grt.Constants.TAG;
import static io.dangernoodle.grt.Constants.TAG_OPT;
import static io.dangernoodle.grt.Constants.USERNAME;
import static io.dangernoodle.grt.Constants.USERNAME_OPT;
import static io.dangernoodle.grt.Constants.WILDCARD;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;


public final class CommandArguments
{
    public static ParameterException createParameterException(CommandSpec spec, String message)
    {
        return new ParameterException(spec.commandLine(), message);
    }

    public static abstract class Arg
    {
        @Spec
        private CommandSpec spec;

        public abstract Map<Object, Object> toArgMap();

        protected ParameterException createParameterException(String message)
        {
            return CommandArguments.createParameterException(spec, message);
        }
    }

    public static class DefininitionOrAll extends Definition
    {
        @Option(names = ALL_OPT)
        private boolean all;

        @Override
        public String getDefintion()
        {
            return all ? WILDCARD : super.getDefintion();
        }
    }

    public static class Definition
    {
        @Parameters(index = "0")
        private String definition;

        public String getDefintion()
        {
            return definition;
        }
    }

    public static class Ref extends Arg
    {
        private String ref;

        @Option(names = REF_OPT, required = true)
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

    public static class Sha1orTag extends Arg
    {
        private Map<Object, Object> args = new HashMap<>();

        @Option(names = SHA1_OPT)
        public void setSha1(String sha1) throws ParameterException
        {
            args.put(SHA1, Optional.of(sha1)
                                   .filter(s -> s.length() > 11)
                                   .orElseThrow(() -> createParameterException("sha1 length must be >= 12")));
        }

        @Option(names = TAG_OPT)
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

    public static class UsernamePassword extends Arg
    {
        @Option(names = PASSWORD_OPT, interactive = true, required = true)
        private char[] password;

        @Option(names = USERNAME_OPT, required = true)
        private String username;

        public char[] getPassword()
        {
            return password;
        }

        public String getUsername()
        {
            return username;
        }

        @Override
        public Map<Object, Object> toArgMap()
        {
            return Map.of(USERNAME, username, PASSWORD, password);
        }
    }
}
