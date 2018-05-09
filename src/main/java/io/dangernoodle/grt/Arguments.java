package io.dangernoodle.grt;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;


@Parameters(resourceBundle = "GithubRepositoryTools")
public class Arguments
{
    @Parameter(names = "--repoDir", descriptionKey = "repoDir", required = true)
    private String repoDir;

    public String getRepoDir()
    {
        return repoDir;
    }
}
