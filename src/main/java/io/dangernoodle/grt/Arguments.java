package io.dangernoodle.grt;

import java.nio.file.Path;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;


@Parameters(resourceBundle = "GithubRepositoryTools")
public class Arguments
{
    @Parameter(names = "--repoDir", descriptionKey = "repoDir", required = true)
    private String repoDir;

    private boolean ignoreErrors;

    private String command;

    public Path getRepoDir()
    {
        return Path.of(repoDir);
    }

    public void setCommand(String command)
    {
        this.command = command;
    }

    public String getCommand()
    {
        return command;
    }

    public boolean isIgnoreErrors()
    {
        return ignoreErrors;
    }

    public void setIgnoreErrors(boolean ignoreErrors)
    {
        this.ignoreErrors = ignoreErrors;
    }
}
