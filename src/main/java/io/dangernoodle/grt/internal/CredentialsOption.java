package io.dangernoodle.grt.internal;

import static io.dangernoodle.grt.Constants.*;

import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;

import io.dangernoodle.grt.cli.options.CommandOption;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Option;


public class CredentialsOption extends CommandOption
{
    @ArgGroup(exclusive = true, multiplicity = "1")
    private Exclusive ex;

    @Override
    public Map<Object, Object> toArgMap()
    {
        return Collections.emptyMap();
    }

    private static class Exclusive
    {
        @ArgGroup
        private GithubApp githubApp;

        @Option(names = OAUTH_OPT)
        private String token;
    }

    private static class GithubApp
    {
        @ArgGroup(exclusive = true)
        private Exclusive ex;

        private static class App
        {
            @Option(names = APP_ID_OPT , required = true)
            private String appId;

            @Option(names = INSTALL_ID_OPT, required = true)
            private String installId;

            @Option(names = APP_KEY_OPT)
            private Path privateKey;
        }

        private static class Exclusive
        {
            @ArgGroup
            private App app;

            @Option(names = APP_OPT)
            private boolean isApp;
        }
    }
}
