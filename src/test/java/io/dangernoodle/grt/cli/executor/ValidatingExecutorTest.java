package io.dangernoodle.grt.cli.executor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.inject.Injector;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.dangernoodle.grt.Command;
import io.dangernoodle.grt.cli.ValidateCommand;


public class ValidatingExecutorTest
{
    private ValidatingExecutor executor;

    @Mock
    private Injector mockInjector;

    @Mock
    private ValidationExecutor mockValidationExecutor;

    @Mock
    private DefinitionExecutor mockDefinitionExecutor;

    @Mock
    private Command mockCommand;

    @BeforeEach
    public void beforeEach()
    {
        MockitoAnnotations.initMocks(this);

        when(mockInjector.getInstance(DefinitionExecutor.class)).thenReturn(mockDefinitionExecutor);
        when(mockInjector.getInstance(ValidationExecutor.class)).thenReturn(mockValidationExecutor);
        
        executor = new ValidatingExecutor(mockInjector);
    }

    @Test
    public void testExecution() throws Exception
    {
        whenExecuteCommand();
        thenValidationExecuted();
        thenDelegateExecuted();
    }

    private void thenDelegateExecuted() throws Exception
    {
        verify(mockDefinitionExecutor).execute(mockCommand);
    }

    private void thenValidationExecuted() throws Exception
    {
        verify(mockValidationExecutor).execute(any(ValidateCommand.class));
    }

    private void whenExecuteCommand() throws Exception
    {
        executor.execute(mockCommand);
    }
}
