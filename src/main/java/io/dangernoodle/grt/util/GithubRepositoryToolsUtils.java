package io.dangernoodle.grt.util;

import org.kohsuke.github.GHCommit;


/**
 * @since 0.8.0
 */
public final class GithubRepositoryToolsUtils
{
    /**
     * @since 0.9.0
     */
    public static String toHex(byte[] array)
    {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < array.length; i++)
        {
            builder.append(byteToHex(array[i]));
        }

        return builder.toString();
    }

    public static String toSha1(GHCommit ghCommit)
    {
        return toSha1(ghCommit, 12);
    }

    public static String toSha1(GHCommit ghCommit, int length)
    {
        return ghCommit.getSHA1().substring(0, length);
    }

    private static String byteToHex(byte num)
    {
        char[] hexDigits = new char[2];
        hexDigits[0] = Character.forDigit((num >> 4) & 0xF, 16);
        hexDigits[1] = Character.forDigit((num & 0xF), 16);

        return new String(hexDigits);
    }
}
