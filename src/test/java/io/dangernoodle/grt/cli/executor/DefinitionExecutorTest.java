package io.dangernoodle.grt.cli.executor;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.FileNotFoundException;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.dangernoodle.grt.Command;
import io.dangernoodle.grt.Workflow;
import io.dangernoodle.grt.util.DefinitionFileVisitor;
import io.dangernoodle.grt.util.DefinitionFileVisitor.Handler;


public class DefinitionExecutorTest
{
    private static final Path ROOT = Path.of("/does/not/exist");

    private DefinitionExecutor executor;

    @Mock
    private Command mockCommand;

    @Mock
    private DefinitionFileVisitor mockVisitor;

    @Mock
    private Workflow<Path> mockWorkflow;

    @BeforeEach
    public void beforeEach()
    {
        MockitoAnnotations.initMocks(this);

        executor = new DefinitionExecutor(ROOT, mockWorkflow)
        {
            @Override
            DefinitionFileVisitor visitor(String definition)
            {
                return mockVisitor;
            }
        };
    }

    @Test
    public void testExecution() throws Exception
    {
        givenASuccessfulVisit();
        whenExecuteCommand();
        thenWorkflowExecuted();
    }

    @Test
    public void testNoneFound() throws Exception
    {
        givenAnUnsuccessfulVisit();
        thenFileNotFoundThrown();
        thenPrePostExecuted();
    }

    private void givenAnUnsuccessfulVisit() throws Exception
    {
        when(mockVisitor.visit(eq(ROOT), any(Handler.class))).thenReturn(0);
    }

    private void givenASuccessfulVisit() throws Exception
    {
        when(mockVisitor.visit(eq(ROOT), any(Handler.class))).thenReturn(1);
    }

    private void thenFileNotFoundThrown()
    {
        assertThrows(FileNotFoundException.class, this::whenExecuteCommand);
    }

    private void thenPrePostExecuted() throws Exception
    {
        verify(mockWorkflow).preExecution();
        verify(mockWorkflow).postExecution();
    }

    private void thenWorkflowExecuted() throws Exception
    {
        verify(mockWorkflow).preExecution();
        verify(mockVisitor).visit(eq(ROOT), any(Handler.class));
        verify(mockWorkflow).postExecution();

        verify(mockWorkflow).execute(eq(ROOT), any(Workflow.Context.class));
        verify(mockCommand).toArgMap();
    }

    private void whenExecuteCommand() throws Exception
    {
        executor.execute(mockCommand);
        // the visitor is a mock, so invoke by hand
        executor.executeWorkflow(ROOT, mockCommand);
    }
}
