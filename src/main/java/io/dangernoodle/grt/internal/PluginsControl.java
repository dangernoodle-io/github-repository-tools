package io.dangernoodle.grt.internal;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.ResourceBundle.Control;
import java.util.stream.Collectors;


class PluginsControl extends Control
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
        return new PluginsBundle(collectBundles(locale));
    }

    private List<ResourceBundle> collectBundles(Locale locale)
    {
        return bundles.stream()
                      .map(bundle -> ResourceBundle.getBundle(bundle, locale))
                      .collect(Collectors.toList());
    }
}
