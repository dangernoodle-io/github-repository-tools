package io.dangernoodle.grt.cli;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.dangernoodle.grt.Arguments;
import io.dangernoodle.grt.json.JsonSchemaValidator;


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
    private JsonSchemaValidator mockValidator;

    @BeforeEach
    public void beforeEach()
    {
        MockitoAnnotations.initMocks(this);
        executor = new ValidateCommand.Executor(mockArguments, mockValidator);
    }

    @Test
    public void testValidateSchema() throws Exception
    {
        whenValidateSchema();
        thenSchemaWasValidated();
    }

    private void thenSchemaWasValidated() throws IOException
    {
        verify(mockValidator, times(2)).validate(any());
    }

    private void whenValidateSchema() throws Exception
    {
        executor.execute(mockFile1, mockFile2);
    }
}
