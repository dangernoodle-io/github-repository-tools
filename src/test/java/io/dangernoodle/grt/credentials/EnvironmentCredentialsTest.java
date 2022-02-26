package io.dangernoodle.grt.credentials;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.dangernoodle.grt.Constants;
import io.dangernoodle.grt.credentials.EnvironmentCredentials;


public class EnvironmentCredentialsTest
{
    private Map<String, String> actualNameValue;

    private String actualToken;

    private String expectedToken;

    private String name;

    private Map<String, Collection<String>> nameValue;

    private Map<String, String> tokens;

    @BeforeEach
    public void beforeEach()
    {
        tokens = new HashMap<>();
    }

    @Test
    public void testGetAuthToken()
    {
        givenTokenCredentials();
        givenGithubTokenWanted();

        whenGetAuthToken();
        thenTokenIsCorrect();

        givenOtherTokenWanted();
        whenGetAuthToken();
        thenTokenIsCorrect();

        givenATokenThatDoesntExist();
        assertThrows(IllegalStateException.class, () -> {
            whenGetAuthToken();
        });
    }

    @Test
    public void testGetNameValue()
    {
        givenNameValues();
        whenGetNameValue();
        thenNameValueMapIsCorrect();
    }

    private void givenATokenThatDoesntExist()
    {
        name = "doesnotexist";
    }

    private void givenGithubTokenWanted()
    {
        name = Constants.GITHUB;
        expectedToken = EnvironmentCredentials.GRT_GITHUB_OAUTH;
    }

    private void givenNameValues()
    {
        name = "env";
        nameValue = Map.of(name, List.of("ENV_NAME1", "ENV_NAME2"));
    }

    private void givenOtherTokenWanted()
    {
        name = "token";
        expectedToken = "TOKEN";
    }

    private void givenTokenCredentials()
    {
        tokens = new HashMap<>();
        tokens.put("token", "TOKEN");
    }

    private void thenNameValueMapIsCorrect()
    {
        assertEquals(2, actualNameValue.size());
        assertEquals("ENV_NAME1", actualNameValue.get("ENV_NAME1"));
        assertEquals("ENV_NAME2", actualNameValue.get("ENV_NAME2"));
    }

    private void thenTokenIsCorrect()
    {
        assertEquals(expectedToken, actualToken);
    }

    private void whenGetAuthToken()
    {
        actualToken = new EnvironmentCredentials(tokens)
        {
            @Override
            String getEnvironmentVariable(String name)
            {
                return name;
            }
        }.getAuthToken(name);
    }

    private void whenGetNameValue()
    {
        actualNameValue = new EnvironmentCredentials(nameValue, Function.identity())
        {
            @Override
            String getEnvironmentVariable(String name)
            {
                return name;
            }
        }.getNameValue(name);
    }
}
