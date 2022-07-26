package io.dangernoodle.grt.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.dangernoodle.RepositoryFiles;


public class SchemaValidationTest
{
    private Exception exception;

    private RepositoryFiles toValidate;

    private JsonTransformer transformer;

    @BeforeEach
    public void beforeEach() throws Exception
    {
        transformer = new JsonTransformer(Map.of("travis", Optional.empty(), "jenkins", Optional.empty()));
    }

    @Test
    public void testNullBranchProtection() throws Exception
    {
        givenNullBranchProtection();
        whenValidateJson();
        thenJsonIsValid();
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

    private void givenANullWorkflow()
    {
        toValidate = RepositoryFiles.nullWorkflow;
    }

    private void givenAValidJsonFile()
    {
        toValidate = RepositoryFiles.mockRepository;
    }

    private void givenNullBranchProtection()
    {
        toValidate = RepositoryFiles.nullBranchProtection;
    }

    private void thenJsonIsValid()
    {
        assertThat(exception, nullValue());
    }

    private void whenValidateJson() throws URISyntaxException
    {
        try
        {
            transformer.validate(toValidate.getPath());
        }
        catch (IOException e)
        {
            exception = e;
        }
    }
}
