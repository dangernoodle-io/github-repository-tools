package io.dangernoodle.grt.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.dangernoodle.grt.util.DefinitionFileVisitor.Handler;


public class DefinitionFileVisitorTest
{
    private int count;

    private boolean matches;

    @Mock
    private Handler mockHandler;

    private Stream<Path> stream;

    private Path path;

    private String toMatch;

    private Path visited;

    private DefinitionFileVisitor visitor;

    @BeforeEach
    public void beforeEach()
    {
        MockitoAnnotations.initMocks(this);

        visitor = new DefinitionFileVisitor("mock")
        {
            @Override
            Stream<Path> getDefinitions(Path root) throws IOException
            {
                return stream;
            }
        };
    }

    @Test
    public void testExactMatch()
    {
        givenAnExactMatch();
        whenMatchPath();
        thenPathMatches();
    }

    @Test
    public void testNoneMatch()
    {
        givenNoneMatch();
        whenMatchPath();
        thenPathDoesntMatch();
    }

    @Test
    public void testNoPathMatched() throws Exception
    {
        givenNoPathMatched();
        whenVisitPath();
        thenPathIsNotHandled();
    }

    @Test
    public void testPathMatched() throws Exception
    {
        givenAPathMatched();
        whenVisitPath();
        thenPathIsHandled();
    }

    @Test
    public void testWildCare()
    {
        givenAWildCard();
        whenMatchPath();
        thenPathMatches();
    }

    private void givenAnExactMatch()
    {
        toMatch = "exact";
        path = Path.of("/foo/bar/exact.json");
    }

    private void givenAPathMatched()
    {
        path = Path.of("/foo/bar/mock.json");
    }

    private void givenAWildCard()
    {
        toMatch = "*";
        path = Path.of("/foo/bar/file.json");
    }

    private void givenNoneMatch()
    {
        toMatch = "exact";
        path = Path.of("/foo/bar/none");
    }

    private void givenNoPathMatched()
    {
        path = Path.of("__this_should_not_match__");
    }

    private void thenPathDoesntMatch()
    {
        assertFalse(matches);
    }

    private void thenPathIsHandled()
    {
        assertEquals(path, visited);
        assertEquals(1, count);
    }

    private void thenPathIsNotHandled()
    {
        assertNull(visited);
        assertEquals(0, count);
    }

    private void thenPathMatches()
    {
        assertTrue(matches);
    }

    private void whenMatchPath()
    {
        matches = DefinitionFileVisitor.pathMatcher(toMatch)
                                       .matches(path);
    }

    private void whenVisitPath() throws Exception
    {
        stream = Stream.of(path, Path.of("/some/random/path"));

        // root doesn't matter here
        count = visitor.visit(null, p -> visited = p);
    }
}
