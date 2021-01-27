//package io.dangernoodle.grt.cli;
//
//import static org.hamcrest.MatcherAssert.assertThat;
//import static org.hamcrest.Matchers.notNullValue;
//import static org.mockito.Mockito.doThrow;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.Arrays;
//import java.util.Collection;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import io.dangernoodle.grt.Arguments;
//import io.dangernoodle.grt.Repository;
//import io.dangernoodle.grt.Workflow;
//import io.dangernoodle.grt.internal.WorkflowExecutor;
//import io.dangernoodle.grt.utils.JsonTransformer;
//import io.dangernoodle.grt.utils.RepositoryMerger;
//
//
//public class RepositoryCommandTest
//{
//    private Exception exception;
//
//    private RepositoryCommand.Executor executor;
//
//    @Mock
//    private Arguments mockArguments;
//
//    @Mock
//    private File mockFile1;
//
//    @Mock
//    private File mockFile2;
//
//    @Mock
//    private RepositoryMerger mockMerger;
//
//    @Mock
//    private Repository mockOverrides;
//
//    @Mock
//    private Workflow.PrePost mockPrePost;
//
//    @Mock
//    private Repository mockRepository;
//
//    @Mock
//    private JsonTransformer mockTransformer;
//
//    @Mock
//    private WorkflowExecutor mockWorkflowExecutor;
//
//    @BeforeEach
//    public void beforeEach() throws Exception
//    {
//        MockitoAnnotations.initMocks(this);
//
//        when(mockMerger.merge(mockRepository, mockRepository)).thenReturn(mockRepository);
//
//        executor = new RepositoryCommand.Executor(null, mockWorkflowExecutor, mockTransformer,
//                Arrays.asList(mockPrePost, mockPrePost))
//        {
//            @Override
//            protected Collection<File> getRepositories() throws IOException
//            {
//                return Arrays.asList(mockFile2);
//            }
//
//            @Override
//            Repository createRepository(File file) throws IOException
//            {
//                return mockRepository;
//            }
//
//            @Override
//            RepositoryMerger createRepositoryMerger()
//            {
//                return mockMerger;
//            }
//
//            @Override
//            File loadRepositoryDefaults() throws IOException
//            {
//                return mockFile1;
//            }
//        };
//    }
//
//    @Test
//    public void testExecutorSuceeds() throws Exception
//    {
//        whenExecuteCommand();
//        thenRepositoriesMerged();
//        thenPreExecutionInvoked();
//        thenWorkflowExecuted();
//        thenPostExecutionInvoked();
//    }
//
//    @Test
//    public void testWorkflowFails() throws Exception
//    {
//        givenAWorkflowFailure();
//        whenExecuteCommand();
//        thenPreExecutionInvoked();
//        thenRepositoriesMerged();
//        thenWorkflowThrewException();
//        thenPostExecutionInvoked();
//    }
//
//    private void givenAWorkflowFailure() throws Exception
//    {
//        doThrow(Exception.class).when(mockWorkflowExecutor).execute(mockRepository);
//    }
//
//    private void thenPostExecutionInvoked() throws Exception
//    {
//        verify(mockPrePost, times(2)).postExecution();
//    }
//
//    private void thenPreExecutionInvoked() throws Exception
//    {
//        verify(mockPrePost, times(2)).preExecution();
//    }
//
//    private void thenRepositoriesMerged()
//    {
//        verify(mockMerger).merge(mockRepository, mockRepository);
//    }
//
//    private void thenWorkflowExecuted() throws Exception
//    {
//        verify(mockWorkflowExecutor).execute(mockRepository);
//    }
//
//    private void thenWorkflowThrewException()
//    {
//        assertThat(exception, notNullValue());
//    }
//
//    private void whenExecuteCommand()
//    {
//        try
//        {
//            executor.execute();
//        }
//        catch (Exception e)
//        {
//            exception = e;
//        }
//    }
//}
