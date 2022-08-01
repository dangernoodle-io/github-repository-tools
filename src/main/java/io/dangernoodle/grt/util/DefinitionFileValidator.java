package io.dangernoodle.grt.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DefinitionFileValidator
{
   
    @SuppressWarnings("WEAK_MESSAGE_DIGEST_MD5")
    MessageDigest createMessageDigest()
    {
        try
        {
            return MessageDigest.getInstance("MD5");
        }
        catch (NoSuchAlgorithmException e)
        {
            // shouldn't happen...
            throw new RuntimeException(e);
        }
    }
}
