package io.dangernoodle.grt.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.dangernoodle.grt.Plugin;


public class BootstrapperTest
{
    private Bootstrapper bootstrapper;

    @Mock
    private CorePlugin mockPlugin;

    @BeforeEach
    public void beforeEach()
    {
        MockitoAnnotations.initMocks(this);

        bootstrapper = new Bootstrapper()
        {
            @Override
            Plugin createCorePlugin()
            {
                // mockPlugin must be a 'CorePlugin' otherwise it's not filtered
                when(mockPlugin.getResourceBundle()).thenReturn(Optional.of("GithubRepositoryTools"));
                return mockPlugin;
            }
        };
    }

    @Test
    public void testGetPluginSchemas()
    {
        Map<String, Optional<String>> schemas = bootstrapper.getPluginSchemas();
        assertEquals(2, schemas.size());

        assertTrue(schemas.containsKey("TestPlugin1"));
        assertTrue(schemas.get("TestPlugin1").get().equals("/TestPlugin1.json"));

        assertTrue(schemas.containsKey("TestPlugin2"));
        assertTrue(schemas.get("TestPlugin2").isEmpty());
    }

    @Test
    public void testGetResourceBundle()
    {
        ResourceBundle bundle = bootstrapper.getResourceBundle();

        assertNotNull(bundle);
        assertFalse(bundle.keySet().isEmpty());
    }
}
