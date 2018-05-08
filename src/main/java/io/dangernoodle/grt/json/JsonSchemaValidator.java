package io.dangernoodle.grt.json;

import java.io.IOException;
import java.io.InputStream;


public interface JsonSchemaValidator
{
    void validate(InputStreamSupplier supplier) throws IOException;

    @FunctionalInterface
    public static interface InputStreamSupplier
    {
        InputStream get() throws IOException;
    }
}
