package io.dangernoodle.grt;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import io.dangernoodle.RepositoryFiles;


public class CredentialsTest
{
    private Credentials credentials;

    private File file;

    @Test
    public void testLoadCredentials() throws Exception
    {
        givenACredentialsFile();
        whenLoadCredentials();
        theCredentialsAreLoaded();
        thenGithubTokenIsFound();
    }

    private void givenACredentialsFile()
    {
        file = RepositoryFiles.credentials.getFile();
    }

    private void theCredentialsAreLoaded()
    {
        assertThat(credentials, notNullValue());
    }

    private void thenGithubTokenIsFound()
    {
        assertThat(credentials.getGithubToken(), equalTo("oauth-token"));
    }
    
    private void whenLoadCredentials() throws IOException
    {
        credentials = Credentials.load(file);
    }
}
