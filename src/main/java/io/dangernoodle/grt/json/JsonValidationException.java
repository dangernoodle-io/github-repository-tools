package io.dangernoodle.grt.json;

import java.io.IOException;


public class JsonValidationException extends IOException
{
    private static final long serialVersionUID = 6527328725281425728L;

    public JsonValidationException(Throwable cause)
    {
        super(cause);
    }
}
