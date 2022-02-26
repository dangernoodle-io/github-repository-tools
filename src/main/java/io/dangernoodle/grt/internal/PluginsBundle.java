package io.dangernoodle.grt.internal;

import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
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
                                         .filter(delegate -> delegate != null)
                                         .flatMap(delegate -> Collections.list(delegate.getKeys()).stream())
                                         .collect(Collectors.toList());

        return Collections.enumeration(keys);
    }

    @Override
    protected Object handleGetObject(String key)
    {
        return bundles.stream()
                      .filter(delegate -> delegate != null && delegate.containsKey(key))
                      .map(delegate -> delegate.getObject(key))
                      .findFirst()
                      .orElse(null);
    }
}
