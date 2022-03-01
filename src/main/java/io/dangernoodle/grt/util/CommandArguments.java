package io.dangernoodle.grt.util;

import static io.dangernoodle.grt.Constants.ALL_OPT;
import static io.dangernoodle.grt.Constants.FILTER_OPT;
import static io.dangernoodle.grt.Constants.SHA1_OPT;
import static io.dangernoodle.grt.Constants.TAG_OPT;
import static io.dangernoodle.grt.Constants.WILDCARD;

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

    public static class Arg
    {
        @Spec
        private CommandSpec spec;

        protected ParameterException createParameterException(String message)
        {
            return CommandArguments.createParameterException(spec, message);
        }
    }

    public static class DefininitionOrAll extends Definition
    {
        @Option(names = ALL_OPT)
        private boolean all;

        @Option(names = FILTER_OPT, required = false)
        private String filter;

        @Override
        public String getDefintion()
        {
            return all ? applyFilter() : super.getDefintion();
        }

        private String applyFilter()
        {
            return Optional.ofNullable(filter)
                           .map(filter -> filter + "/" + WILDCARD)
                           .orElse(WILDCARD);
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

    public static class Sha1orTag extends Arg
    {
        private String sha1;

        @Option(names = TAG_OPT)
        private String tag;

        public String getSha1()
        {
            return sha1;
        }

        public String getTag()
        {
            return tag;
        }

        @Option(names = SHA1_OPT)
        public void setSha1(String sha1) throws ParameterException
        {
            this.sha1 = Optional.of(sha1)
                                .filter(s -> s.length() > 11)
                                .orElseThrow(() -> createParameterException("sha1 length must be >= 12"));
        }
    }
}
