package io.dangernoodle.grt.util;

import java.io.IOException;
import java.io.Reader;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Security;
import java.security.spec.PKCS8EncodedKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.kohsuke.github.GHCommit;


/**
 * @since 0.8.0
 */
public final class GithubRepositoryToolsUtils
{
    static
    {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * @since 0.9.0
     */
    public static PrivateKey readPrivateKey(Reader reader) throws GeneralSecurityException, IOException
    {
        try (PemReader pemReader = new PemReader(reader))
        {
            PemObject pemObject = pemReader.readPemObject();
            byte[] content = pemObject.getContent();

            return KeyFactory.getInstance("RSA")
                             .generatePrivate(new PKCS8EncodedKeySpec(content));
        }
    }

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
