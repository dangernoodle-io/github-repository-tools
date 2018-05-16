package io.dangernoodle.grt.internal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.FileAlreadyExistsException;

import org.junit.jupiter.api.Test;


public class FileLoaderTest
{
    private Exception exception;

    private FileLoader loader;

    private String repoName;

    private File result;

    @Test
    public void testLoadCredentials()
    {
        givenAValidRootDir();
        whenLoadCredentials();
        thenFileIsReturned();
    }

    @Test
    public void testLoadCredentialsNotFound()
    {
        givenAnInvalidRootDir();
        whenLoadCredentials();
        thenFileNotFoundExceptionThrown();
    }

    @Test
    public void testLoadRepository()
    {
        givenAValidRootDir();
        givenARepository();
        whenLoadRepository();
        thenFileIsReturned();
    }

    @Test
    public void testLoadRepositoryDefaults()
    {
        givenAValidRootDir();
        whenLoadRepositoryDefaults();
        thenFileIsReturned();
    }

    @Test
    public void testLoadRepositoryDefaultsNotFound()
    {
        givenAValidRootDir();
        whenLoadRepositoryDefaults();
        thenFileIsReturned();
    }

    @Test
    public void testLoadRepositoryDuplicate()
    {
        givenAValidRootDir();
        givenARepositoryDuplicate();
        whenLoadRepository();
        thenFileExistsExceptionThrown();
    }

    @Test
    public void testLoadRepositoryNotFound()
    {
        givenAValidRootDir();
        givenARepositoryThatDoesntExist();
        whenLoadRepository();
        thenFileNotFoundExceptionThrown();
    }

    private File getCurrentWorkingDir()
    {
        return new File(FileLoaderTest.class.getProtectionDomain().getCodeSource().getLocation().getFile());
    }

    private void givenAnInvalidRootDir()
    {
        loader = new FileLoader(getCurrentWorkingDir().toString());
    }

    private void givenARepository()
    {
        repoName = "repository";
    }

    private void givenARepositoryDuplicate()
    {
        repoName = "duplicate";
    }

    private void givenARepositoryThatDoesntExist()
    {
        repoName = "doesnotexist";
    }

    private void givenAValidRootDir()
    {
        loader = new FileLoader(new File(getCurrentWorkingDir(), "repositories").toString());
    }

    private void thenFileExistsExceptionThrown()
    {
        assertThat(exception, notNullValue());
        assertThat(exception, instanceOf(FileAlreadyExistsException.class));
    }

    private void thenFileIsReturned()
    {
        assertThat(result, notNullValue());
    }

    private void thenFileNotFoundExceptionThrown()
    {
        assertThat(exception, notNullValue());
        assertThat(exception, instanceOf(FileNotFoundException.class));
    }

    private void whenLoadCredentials()
    {
        try
        {
            result = loader.loadCredentials();
        }
        catch (Exception e)
        {
            exception = e;
        }
    }

    private void whenLoadRepository()
    {
        try
        {
            result = loader.loadRepository(repoName);
        }
        catch (Exception e)
        {
            exception = e;
        }
    }

    private void whenLoadRepositoryDefaults()
    {
        try
        {
            result = loader.loadRepositoryDefaults();
        }
        catch (Exception e)
        {
            exception = e;
        }
    }
}
