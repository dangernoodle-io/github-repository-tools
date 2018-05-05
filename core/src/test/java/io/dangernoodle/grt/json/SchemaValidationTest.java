package io.dangernoodle.grt.json;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;

import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.dangernoodle.grt.internal.EveritSchemaValidator;


public class SchemaValidationTest
{
    private Exception exception;
    
    private String toValidate;

    private EveritSchemaValidator validator;

    @BeforeEach
    public void beforeEach() throws Exception
    {
        validator = new EveritSchemaValidator(() -> getInputStream("/repository-schema.json"));
    }

    private InputStream getInputStream(String path)
    {
        return getClass().getResourceAsStream(path);
    }
    
    @Test
    public void testSchemaIsValid() throws Exception
    {
        givenAValidJsonFile();
        whenValidateJson();
        thenJsonIsValid();
    }

    private void givenAValidJsonFile()
    {
        toValidate = "/test-files/mockRepository.json";
    }

    private void thenJsonIsValid()
    {
        assertThat(exception, nullValue());
    }

    private void whenValidateJson()
    {
        try
        {
            validator.validate(() -> getInputStream(toValidate));
        }
        catch (IOException e)
        {
            exception = e;
        }
    }
}
