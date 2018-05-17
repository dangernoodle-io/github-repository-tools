package io.dangernoodle.grt.cli;

import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.dangernoodle.grt.Arguments;
import io.dangernoodle.grt.utils.JsonTransformer;


public class ValidateCommandTest
{
    private ValidateCommand.Executor executor;

    @Mock
    private Arguments mockArguments;

    @Mock
    private File mockFile1;

    @Mock
    private File mockFile2;

    @Mock
    private JsonTransformer mockTransformer;

    @BeforeEach
    public void beforeEach()
    {
        MockitoAnnotations.initMocks(this);
        executor = new ValidateCommand.Executor(mockArguments, mockTransformer);
    }

    @Test
    public void testValidateSchema() throws Exception
    {
        whenValidateSchema();
        thenSchemaWasValidated();
    }

    private void thenSchemaWasValidated() throws IOException
    {
        verify(mockTransformer).validate(mockFile1);
        verify(mockTransformer).validate(mockFile2);
    }

    private void whenValidateSchema() throws Exception
    {
        executor.execute(mockFile1, mockFile2);
    }
}
