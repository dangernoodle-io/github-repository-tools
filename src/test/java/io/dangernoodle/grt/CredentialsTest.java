package io.dangernoodle.grt;

import static io.dangernoodle.grt.Repository.GITHUB;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import io.dangernoodle.TestFiles;


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
        thenGithutTokenRemoved();
    }

    private void givenACredentialsFile()
    {
        file = TestFiles.credentials.getFile();
    }

    private void theCredentialsAreLoaded()
    {
        assertThat(credentials, notNullValue());
    }

    private void thenGithubTokenIsFound()
    {
        assertThat(credentials.getGithubToken(), equalTo("oauth-token"));
    }

    private void thenGithutTokenRemoved()
    {
        assertThat(credentials.getCredentials(GITHUB), nullValue());
    }

    private void whenLoadCredentials() throws IOException
    {
        credentials = Credentials.load(file);
    }
}
