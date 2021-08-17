package io.dangernoodle.grt.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kohsuke.github.GHCommit;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


public class GithubRepositoryToolsUtilsTest
{
    private static final String COMMIT = "3ac68f50226751ae44654497a38e220437ee677";

    @Mock
    private GHCommit mockCommit;

    @BeforeEach
    public void beforeEach()
    {
        MockitoAnnotations.initMocks(this);

        when(mockCommit.getSHA1()).thenReturn(COMMIT);
    }

    @Test
    public void testToSha1()
    {
        assertEquals("3ac68f502267", GithubRepositoryToolsUtils.toSha1(mockCommit));
    }
}