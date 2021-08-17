package io.dangernoodle.grt.utils;

import org.kohsuke.github.GHCommit;


public final class GithubRepositoryToolsUtils
{
    public static String toSha1(GHCommit ghCommit)
    {
        return toSha1(ghCommit, 12);
    }

    public static String toSha1(GHCommit ghCommit, int length)
    {
        return ghCommit.getSHA1().substring(0, length);
    }
}
