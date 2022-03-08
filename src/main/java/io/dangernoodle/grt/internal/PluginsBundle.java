package io.dangernoodle.grt.internal;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;


class PluginsBundle extends ResourceBundle
{
    private final Collection<ResourceBundle> bundles;

    PluginsBundle(Collection<ResourceBundle> bundles)
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

    private static class PluginsControl extends Control
    {
        private final Collection<String> bundles;

        PluginsControl(Collection<String> bundles)
        {
            this.bundles = bundles;
        }

        @Override
        public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload)
            throws IllegalAccessException, InstantiationException, IOException
        {
            return new PluginsBundle(collectBundles(loader, locale));
        }

        private List<ResourceBundle> collectBundles(ClassLoader loader, Locale locale)
        {
            return bundles.stream()
                          .map(bundle -> ResourceBundle.getBundle(bundle, locale, loader))
                          .collect(Collectors.toList());
        }
    }

    static ResourceBundle merge(Collection<String> bundles)
    {
        return ResourceBundle.getBundle("grt", Locale.getDefault(), new PluginsControl(bundles));
    }
}
