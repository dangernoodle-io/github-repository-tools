package io.dangernoodle.grt.cli;

import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

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
        executor = new ValidateCommand.Executor(mockArguments, mockTransformer)
        {
            @Override
            protected Collection<File> getRepositories() throws IOException
            {
                return Arrays.asList(mockFile2);
            }

            @Override
            protected File loadRepositoryDefaults() throws IOException
            {
                return mockFile1;
            }
        };
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
        executor.execute();
    }
}
