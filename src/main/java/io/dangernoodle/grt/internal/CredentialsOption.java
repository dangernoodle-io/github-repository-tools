package io.dangernoodle.grt.internal;

import static io.dangernoodle.grt.Constants.APP;
import static io.dangernoodle.grt.Constants.APP_ID;
import static io.dangernoodle.grt.Constants.APP_ID_OPT;
import static io.dangernoodle.grt.Constants.APP_KEY;
import static io.dangernoodle.grt.Constants.APP_KEY_OPT;
import static io.dangernoodle.grt.Constants.APP_OPT;
import static io.dangernoodle.grt.Constants.INSTALL_ID;
import static io.dangernoodle.grt.Constants.INSTALL_ID_OPT;
import static io.dangernoodle.grt.Constants.OAUTH;
import static io.dangernoodle.grt.Constants.OAUTH_OPT;

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

        @Option(names = OAUTH_OPT, descriptionKey = OAUTH)
        private String token;
    }

    private static class GithubApp
    {
        @ArgGroup(exclusive = true)
        private Exclusive ex;

        private static class App
        {
            @Option(names = APP_ID_OPT, descriptionKey = APP_ID, required = true)
            private String appId;

            @Option(names = INSTALL_ID_OPT, descriptionKey = INSTALL_ID, required = true)
            private String installId;

            @Option(names = APP_KEY_OPT, descriptionKey = APP_KEY)
            private Path privateKey;
        }

        private static class Exclusive
        {
            @ArgGroup
            private App app;

            @Option(names = APP_OPT, descriptionKey = APP)
            private boolean isApp;
        }
    }
}
