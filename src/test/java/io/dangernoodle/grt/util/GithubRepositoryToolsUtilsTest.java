package io.dangernoodle.grt.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.io.InputStreamReader;
import java.io.Reader;
import java.security.PrivateKey;

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
    public void testReadPrivateKey() throws Exception
    {
        Reader reader = new InputStreamReader(getClass().getResourceAsStream("/dummy-key.pem")); 
        PrivateKey key = GithubRepositoryToolsUtils.readPrivateKey(reader);
        
        assertNotNull(key);
        assertEquals("PKCS#8", key.getFormat());
    }

    @Test
    public void testToHex()
    {
        assertEquals("746f486578", GithubRepositoryToolsUtils.toHex("toHex".getBytes()));
    }
    
    @Test
    public void testToSha1()
    {
        assertEquals("3ac68f502267", GithubRepositoryToolsUtils.toSha1(mockCommit));
    }

    
}
