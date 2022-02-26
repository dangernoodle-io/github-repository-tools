package io.dangernoodle.grt.util;

import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;


public class CommandLineHelpers
{
    public static class DefininitionOrAll
    {
        @Option(names = "--all")
        private boolean all;

        @Parameters(index = "0")
        private String definition;

        public String getDefintion()
        {
            return all ? "*" : definition;
        }
    }
}
