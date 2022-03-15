package io.dangernoodle.grt.credentials;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.dangernoodle.RepositoryFiles;
import io.dangernoodle.grt.Credentials;
import io.dangernoodle.grt.util.JsonTransformer;


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
    public void testEmptyCredentials() throws Exception
    {
        givenEmptyCredentialsFile();
        givenACredentialsFile();
        whenLoadCredentials();
        thenGithubTokenMissing();
    }

    @Test
    public void testGithubApp() throws Exception
    {
        givenACredentialsFile();
        whenLoadCredentials();
        thenCredentialsAreLoaded();
        thenGithupAppNotFound();
    }

    @Test
    public void testGithubOAuthToken() throws Exception
    {
        givenACredentialsFile();
        whenLoadCredentials();
        thenCredentialsAreLoaded();
        thenGithubTokenIsFound();
        thenCredentialsMapIsFound();
    }

    @Test
    public void testNullCredentials() throws Exception
    {
        givenNoCredentialsFile();
        thenIOExceptionThrown();
    }
    
    @Test
    public void testRunAsApp() throws Exception
    {
        whenLoadCredentials();
        thenRunAsAppIsFalse();
    }

    private void givenACredentialsFile() throws IOException, URISyntaxException
    {
        when(mockTransformer.deserialize(file.getPath())).thenReturn(file.toJsonObject());
    }

    private void givenEmptyCredentialsFile()
    {
        file = RepositoryFiles.emptyCredentials;
    }

    private void givenNoCredentialsFile() throws IOException, URISyntaxException
    {
        when(mockTransformer.deserialize(file.getPath())).thenThrow(new IOException());
    }
    
    private void thenCredentialsAreLoaded()
    {
        assertNotNull(credentials);
    }

    private void thenCredentialsMapIsFound()
    {
        Map<String, Object> map = credentials.getNameValue("map");

        assertNotNull(map);
        assertEquals("user", "user");
    }

    private void thenGithubTokenIsFound()
    {
        assertEquals("oauth-token", credentials.getGithubOAuthToken());
    }

    private void thenGithubTokenMissing()
    {
        assertThrows(IllegalStateException.class, () -> credentials.getGithubOAuthToken());
    }

    private void thenGithupAppNotFound()
    {
        assertThrows(IllegalStateException.class, () -> credentials.getGithubApp());
    }

    private void thenIOExceptionThrown()
    {
        assertThrows(IOException.class, this::whenLoadCredentials);
    }

    private void thenRunAsAppIsFalse()
    {
        assertFalse(credentials.runAsApp());
    }

    private void whenLoadCredentials() throws IOException, URISyntaxException
    {
        credentials = new JsonCredentials(mockTransformer.deserialize(file.getPath()));
    }
}
