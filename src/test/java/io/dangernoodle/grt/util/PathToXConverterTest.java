package io.dangernoodle.grt.util;

import static org.mockito.Mockito.verify;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.dangernoodle.grt.Workflow;


public class PathToXConverterTest
{
    private PathToXConverter<Object> converter;
    
    @Mock
    private Workflow.Context mockContext;

    @Mock
    private Workflow<Object> mockWorkflow;

    private Object object;
    
    private Path path;
    
    @BeforeEach
    public void beforeEach()
    {
        MockitoAnnotations.initMocks(this);

        object = new Object();
        converter = new PathToXConverter<>(mockWorkflow, path -> object);
    }

    @Test
    public void testConverter() throws Exception
    {
        givenAPathObject();
        whenExecuteConverter();
        thenPathIsConverted();
        thenDelegateWorkflowExecuted();
    }

    private void givenAPathObject()
    {
        path = Path.of("/");
    }

    private void thenDelegateWorkflowExecuted() throws Exception
    {
        verify(mockWorkflow).preExecution();
        verify(mockWorkflow).postExecution();
    }

    private void thenPathIsConverted() throws Exception
    {
        verify(mockWorkflow).execute(object, mockContext);
    }
    
    private void whenExecuteConverter() throws Exception
    {
        converter.preExecution();
        converter.execute(path, mockContext);
        converter.postExecution();
    }
}
