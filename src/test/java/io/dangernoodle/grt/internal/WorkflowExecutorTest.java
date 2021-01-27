//package io.dangernoodle.grt.internal;
//
//import static org.hamcrest.MatcherAssert.assertThat;
//import static org.hamcrest.Matchers.notNullValue;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.doThrow;
//import static org.mockito.Mockito.inOrder;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//import java.util.Arrays;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InOrder;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import io.dangernoodle.grt.Repository;
//import io.dangernoodle.grt.Workflow;
//import io.dangernoodle.grt.utils.JsonTransformer;
//import io.dangernoodle.grt.utils.RepositoryBuilder;
//
//
//public class WorkflowExecutorTest
//{
//    private Exception exception;
//
//    private WorkflowExecutor executor;
//
//    @Mock
//    private Workflow mockGithubWorkflow;
//
//    @Mock
//    private Workflow mockPlugin1Workflow;
//
//    @Mock
//    private Workflow mockPlugin2Workflow;
//
//    private RepositoryBuilder repoBuilder;
//
//    @Mock
//    private Repository repository;
//
//    @BeforeEach
//    public void beforeEach()
//    {
//        MockitoAnnotations.initMocks(this);
//
//        when(mockGithubWorkflow.getName()).thenReturn("github");
//        when(mockPlugin1Workflow.getName()).thenReturn("plugin1");
//        when(mockPlugin2Workflow.getName()).thenReturn("plugin2");
//
//        repoBuilder = new RepositoryBuilder(new JsonTransformer());
//        executor = new WorkflowExecutor(Arrays.asList(mockGithubWorkflow, mockPlugin1Workflow, mockPlugin2Workflow));
//    }
//
//    @Test
//    public void testGithubOnly() throws Exception
//    {
//        givenAGithubWorkflowOnly();
//        whenExecuteWorkflow();
//        thenOnlyGithubWorkflowCalled();
//    }
//
//    @Test
//    public void testGithubThrowsException() throws Exception
//    {
//        givenAGithubWorkflowOnly();
//        givenAGithubWorkflowException();
//        whenExecuteWorkflow();
//        thenAnExceptionWasThrow();
//        thenOnlyGithubWorkflowCalled();
//    }
//
//    @Test
//    public void testPluginOverridesOrder() throws Exception
//    {
//        givenAnOverridenWorkflowOrder();
//        whenExecuteWorkflow();
//        thenPluginsExecutedInOverridenOrder();
//    }
//
//    @Test
//    public void testWithPlugins() throws Exception
//    {
//        givenAWorkflowWithPlugins();
//        whenExecuteWorkflow();
//        thenAllPluginsExecutedInOrder();
//    }
//
//    @Test
//    public void testWorflowNotFound() throws Exception
//    {
//        givenAWorkflowWithPlugins();
//        givenAnExecutorWithoutPlugins();
//        whenExecuteWorkflow();
//        thenOnlyGithubWorkflowCalled();
//    }
//
//    private void givenAGithubWorkflowException() throws Exception
//    {
//        doThrow(Exception.class).when(mockGithubWorkflow).execute(eq(repository), any());
//    }
//
//    private void givenAGithubWorkflowOnly()
//    {
//        repository = repoBuilder.build();
//    }
//
//    private void givenAnExecutorWithoutPlugins()
//    {
//        executor = new WorkflowExecutor(Arrays.asList(mockGithubWorkflow));
//    }
//
//    private void givenAnOverridenWorkflowOrder()
//    {
//        repository = repoBuilder.addWorkflow("plugin2")
//                                .addWorkflow("github")
//                                .addWorkflow("plugin1")
//                                .build();
//    }
//
//    private void givenAWorkflowWithPlugins()
//    {
//        repository = repoBuilder.addWorkflow("plugin1")
//                                .addWorkflow("plugin2")
//                                .build();
//    }
//
//    private void thenAllPluginsExecutedInOrder() throws Exception
//    {
//        InOrder ordered = inOrder(mockGithubWorkflow, mockPlugin1Workflow, mockPlugin2Workflow);
//
//        ordered.verify(mockGithubWorkflow).execute(eq(repository), any());
//        ordered.verify(mockPlugin1Workflow).execute(eq(repository), any());
//        ordered.verify(mockPlugin2Workflow).execute(eq(repository), any());
//    }
//
//    private void thenAnExceptionWasThrow()
//    {
//        assertThat(exception, notNullValue());
//    }
//
//    private void thenOnlyGithubWorkflowCalled() throws Exception
//    {
//        verify(mockGithubWorkflow).execute(eq(repository), any());
//
//        verify(mockPlugin1Workflow, times(0)).execute(eq(repository), any());
//        verify(mockPlugin2Workflow, times(0)).execute(eq(repository), any());
//    }
//
//    private void thenPluginsExecutedInOverridenOrder() throws Exception
//    {
//        InOrder ordered = inOrder(mockPlugin2Workflow, mockGithubWorkflow, mockPlugin1Workflow);
//
//        ordered.verify(mockPlugin2Workflow).execute(eq(repository), any());
//        ordered.verify(mockGithubWorkflow).execute(eq(repository), any());
//        ordered.verify(mockPlugin1Workflow).execute(eq(repository), any());
//    }
//
//    private void whenExecuteWorkflow()
//    {
//        try
//        {
//            executor.execute(repository);
//        }
//        catch (Exception e)
//        {
//            exception = e;
//        }
//    }
//}
