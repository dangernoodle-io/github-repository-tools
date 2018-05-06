package io.dangernoodle.grt.json;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;

import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.dangernoodle.grt.TestFiles;
import io.dangernoodle.grt.internal.EveritSchemaValidator;


public class SchemaValidationTest
{
    private Exception exception;
    
    private TestFiles toValidate;

    private EveritSchemaValidator validator;

    @BeforeEach
    public void beforeEach() throws Exception
    {
        validator = new EveritSchemaValidator(() -> getInputStream("/repository-schema.json"));
    }

    @Test
    public void testNullWorkflow() throws Exception
    {
        givenANullWorkflow();
        whenValidateJson();
        thenJsonIsValid();
    }
    
    @Test
    public void testSchemaIsValid() throws Exception
    {
        givenAValidJsonFile();
        whenValidateJson();
        thenJsonIsValid();
    }
    
    private InputStream getInputStream(String path)
    {
        return getClass().getResourceAsStream(path);
    }
    
    private void givenANullWorkflow()
    {
        toValidate = TestFiles.nullWorkflow;
    }

    private void givenAValidJsonFile()
    {
        toValidate = TestFiles.mockRepository;
    }

    private void thenJsonIsValid()
    {
        assertThat(exception, nullValue());
    }

    private void whenValidateJson()
    {
        try
        {
            validator.validate(() -> toValidate.getInputStream());
        }
        catch (IOException e)
        {
            exception = e;
        }
    }
}
