package io.dangernoodle.grt.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;

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

    @Mock
    private Iterator<Path> mockIterator;

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
            Iterator<Path> iterator(Path root) throws IOException
            {
                return mockIterator;
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
        path = Path.of("matched");

        when(mockIterator.hasNext()).thenReturn(true).thenReturn(false);
        when(mockIterator.next()).thenReturn(path);
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
        when(mockIterator.hasNext()).thenReturn(false);
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
        // root doesn't matter here
        count = visitor.visit(null, p -> visited = p);
    }
}
