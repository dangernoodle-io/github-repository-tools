package io.dangernoodle.grt.credentials;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class EnvironmentCredentialsTest
{
    private Map<String, Object> actualNameValue;

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
    public void testGetCredentials()
    {
        givenTokenCredentials();
        whenGetCredentials();
        thenTokenIsCorrect();

        givenOtherTokenWanted();
        whenGetCredentials();
        thenTokenIsCorrect();

        givenATokenThatDoesntExist();
        whenGetCredentials();
        thenTokenIsNull();
    }

    @Test
    public void testGetNameValue()
    {
        givenNameValues();
        whenGetNameValue();
        thenNameValueMapIsCorrect();
    }

    @Test
    public void testRunAsUser()
    {
        assertFalse(new EnvironmentCredentials().runAsApp());
    }

    private void givenATokenThatDoesntExist()
    {
        name = "doesnotexist";
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

    private void thenTokenIsNull()
    {
        assertNull(actualToken);
    }

    private void whenGetCredentials()
    {
        actualToken = new EnvironmentCredentials(tokens)
        {
            @Override
            protected Object getEnvironmentVariable(String name)
            {
                return name;
            }
        }.getCredentials(name);
    }

    private void whenGetNameValue()
    {
        actualNameValue = new EnvironmentCredentials(nameValue, k -> k)
        {
            @Override
            protected Object getEnvironmentVariable(String name)
            {
                return name;
            }
        }.getNameValue(name);
    }
}
