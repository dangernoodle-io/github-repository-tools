package io.dangernoodle.grt.internal;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.stream.Collectors;


class PluginsBundle extends ResourceBundle
{
    private final Collection<ResourceBundle> bundles;

    private PluginsBundle(Collection<ResourceBundle> bundles)
    {
        this.bundles = bundles;
    }

    @Override
    public Enumeration<String> getKeys()
    {
        Collection<String> keys = bundles.stream()
                                         .flatMap(delegate -> Collections.list(delegate.getKeys()).stream())
                                         .collect(Collectors.toList());

        return Collections.enumeration(keys);
    }

    @Override
    protected Object handleGetObject(String key)
    {
        return bundles.stream()
                      .filter(delegate -> delegate.containsKey(key))
                      .map(delegate -> delegate.getObject(key))
                      .findFirst()
                      .orElse(null);
    }

    static ResourceBundle merge(Collection<String> bundles)
    {
        return ResourceBundle.getBundle("grt", Locale.getDefault(), new PluginsControl(bundles));
    }

    private static class PluginsControl extends Control
    {
        private final Collection<String> names;

        PluginsControl(Collection<String> names)
        {
            this.names = names;
        }

        @Override
        public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload)
            throws IllegalAccessException, InstantiationException, IOException
        {
            return new PluginsBundle(collectBundles(loader, locale));
        }

        private List<ResourceBundle> collectBundles(ClassLoader loader, Locale locale)
        {
            return names.stream()
                        .map(name -> loadBundle(name, locale, loader))
                        .collect(Collectors.toList());
        }

        private ResourceBundle loadBundle(String name, Locale locale, ClassLoader loader)
        {
            try
            {
                return ResourceBundle.getBundle(name, locale, loader);
            }
            catch (MissingResourceException e)
            {
                // wrap and re-throw this so we can tell what bundle is actually missing
                throw new IllegalStateException(e);
            }
        }
    }
}
