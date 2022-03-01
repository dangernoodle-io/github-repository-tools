//package io.dangernoodle.grt.workflow.step;
//
//import static io.dangernoodle.grt.Constants.TAG;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//import java.io.IOException;
//import java.util.Spliterator;
//import java.util.stream.Stream;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.kohsuke.github.GHCommit;
//import org.kohsuke.github.GHRepository;
//import org.kohsuke.github.GHTag;
//import org.mockito.Mock;
//
//
//public class FindCommitByTagTest extends AbstractGithubWorkflowStepTest
//{
//    private static final String REPO_TAG = "repo-1.0.0";
//
//    @Mock
//    private GHCommit mockCommit;
//
//    @Mock
//    private Spliterator<GHTag> mockSpliterator;
//
//    @Mock
//    private GHTag mockTag;
//
//    @Override
//    @BeforeEach
//    public void beforeEach() throws Exception
//    {
//        super.beforeEach();
//
//        when(mockCommit.getSHA1()).thenReturn(COMMIT);
//        when(mockTag.getCommit()).thenReturn(mockCommit);
//        when(mockContext.get(TAG)).thenReturn(REPO_TAG);
//    }
//
//    @Test
//    public void testTagDoesNotExist()
//    {
//        assertThrows(IllegalStateException.class, () -> {
//            whenExecuteStep();
//        });
//    }
//
//    @Test
//    public void testTagExists() throws Exception
//    {
//        givenATagExists();
//        whenExecuteStep();
//        thenCommitIsFound();
//        thenStatusIsContinue();
//    }
//
//    @Override
//    protected AbstractGithubStep createStep()
//    {
//        return new FindCommitByTag(mockClient)
//        {
//            @Override
//            Stream<GHTag> getTagStream(GHRepository ghRepo) throws IOException
//            {
//                return Stream.of(mockTag);
//            }
//        };
//    }
//
//    private void givenATagExists()
//    {
//        when(mockTag.getName()).thenReturn(REPO_TAG);
//    }
//
//    private void thenCommitIsFound()
//    {
//        verify(mockContext).add(mockCommit);
//    }
//}
