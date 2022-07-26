package io.dangernoodle.grt.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.ResourceBundle;

import org.junit.jupiter.api.Test;


public class PluginBundleTest
{
    private ResourceBundle bundle;

    private List<String> bundles;

    @Test
    public void testBundle()
    {
        givenBundlesToMerge();
        whenMergeBundles();
        thenBundlesMerged();
    }

    private void givenBundlesToMerge()
    {
        bundles = List.of("GithubRepositoryTools", "TestPlugin1");
    }

    private void thenBundlesMerged()
    {
        assertTrue(bundle.getKeys().hasMoreElements());
        
        assertEquals("Application id", bundle.getString("appId"));
        assertEquals("test", bundle.getString("this.is.a.test.key"));
        assertEquals("Remove any existing webhooks associated with the repository", bundle.getString("grt.repository.clearWebhooks"));
    }

    private void whenMergeBundles()
    {
         bundle = PluginsBundle.merge(bundles);
    }
}
