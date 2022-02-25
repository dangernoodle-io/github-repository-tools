package io.dangernoodle.grt.creds;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.dangernoodle.RepositoryFiles;
import io.dangernoodle.grt.Credentials;
import io.dangernoodle.grt.utils.JsonTransformer;


public class JsonCredentialsTest
{
    private Credentials credentials;

    private RepositoryFiles file;

    @Mock
    private JsonTransformer mockTransformer;

    @BeforeEach
    public void beforeEach()
    {
        MockitoAnnotations.initMocks(this);
        file = RepositoryFiles.credentials;
    }

    @Test
    public void testLoadCredentials() throws Exception
    {
        givenACredentialsFile();
        whenLoadCredentials();
        theCredentialsAreLoaded();
        thenGithubTokenIsFound();
        thenCredentialsMapIsFound();
    }

    @Test
    public void testNullCredentials() throws Exception
    {
        givenNoCredentialsFile();
        whenLoadCredentials();
        thenNullCredentialsAreReturned();
    }

    private void givenACredentialsFile() throws IOException
    {
        when(mockTransformer.deserialize(file.getFile())).thenReturn(file.toJsonObject());
    }

    private void givenNoCredentialsFile() throws IOException
    {
        when(mockTransformer.deserialize(file.getFile())).thenThrow(new FileNotFoundException());
    }

    private void theCredentialsAreLoaded()
    {
        assertNotNull(credentials);
    }

    private void thenCredentialsMapIsFound()
    {
        Map<String, String> map = credentials.getNameValue("map");

        assertNotNull(map);
        assertEquals("user", "user");
    }

    private void thenGithubTokenIsFound()
    {
        assertEquals("oauth-token", credentials.getGithubToken());
    }

    private void thenNullCredentialsAreReturned()
    {
        assertEquals(Credentials.NULL, credentials);
    }

    private void whenLoadCredentials() throws FileNotFoundException
    {
        credentials = new JsonCredentials(mockTransformer.deserialize(file.getFile()));
    }
}
