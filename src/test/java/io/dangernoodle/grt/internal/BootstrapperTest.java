package io.dangernoodle.grt.internal;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.ResourceBundle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.dangernoodle.grt.Plugin;


public class BootstrapperTest
{
    @Mock
    private Plugin mockPlugin;

    private Bootstrapper bootstrapper;

    @BeforeEach
    public void beforeEach()
    {
        MockitoAnnotations.initMocks(this);

        bootstrapper = new Bootstrapper()
        {
            @Override
            Plugin createCorePlugin()
            {
                when(mockPlugin.getResourceBundle()).thenReturn(Optional.of("GithubRepositoryTools"));

                return mockPlugin;
            }
        };
    }

    @Test
    public void testGetResourceBundle()
    {
        ResourceBundle bundle = bootstrapper.getResourceBundle();

        assertNotNull(bundle);
        assertFalse(bundle.keySet().isEmpty());
    }
}
