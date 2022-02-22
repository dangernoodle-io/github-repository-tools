package io.dangernoodle.grt.utils;

import java.io.IOException;
import java.util.Collection;

import org.everit.json.schema.ValidationException;


public class JsonValidationException extends IOException
{
    private static final long serialVersionUID = 6527328725281425728L;

    public JsonValidationException(ValidationException cause)
    {
        super(cause);
    }

    public Collection<String> getValidationErrors()
    {
        return ((ValidationException) getCause()).getAllMessages();
    }
}
