package io.dangernoodle.grt.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.ResourceBundle.Control;
import java.util.ServiceLoader;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.util.Modules;

import io.dangernoodle.grt.Command;
import io.dangernoodle.grt.Plugin;


/**
 * @since 0.9.0
 */
public class PluginsManager
{
    private final Collection<Plugin> plugins;

    public PluginsManager(Plugin core)
    {
        this.plugins = loadPlugins(core);
    }

    public Injector getInjector(Module... modules)
    {
        Module module = Modules.override(collect(Plugin::getModules, List.of(modules)))
                               .with(collect(Plugin::getOverrides));

        return Guice.createInjector(module);
    }

    public ResourceBundle getResourceBundle()
    {
        return null;
    }

    public Collection<Class<? extends Command>> getCommands()
    {
        return collect(Plugin::getCommands);
    }

    private <T> List<T> collect(Function<Plugin, Collection<T>> function)
    {
        return collect(function, Collections.emptyList());
    }

    private <T> List<T> collect(Function<Plugin, Collection<T>> function, Collection<T> additional)
    {
        Stream<T> stream = plugins.stream()
                                  .map(function)
                                  .flatMap(Collection::stream);

        return Stream.concat(stream, additional.stream())
                     .collect(Collectors.toList());
    }

    private Collection<Plugin> loadPlugins(Plugin core)
    {
        // the 'core' plugin is always first
        ArrayList<Plugin> plugins = new ArrayList<>();
        plugins.add(core);

        ServiceLoader.load(Plugin.class)
                     .stream()
                     .map(p -> p.get())
                     .forEach(plugins::add);

        return plugins;
    }

    class PluginResourceBundle extends ResourceBundle
    {
        private final Collection<ResourceBundle> bundles;

        PluginResourceBundle(Collection<ResourceBundle> bundles)
        {
            this.bundles = bundles;
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

        @Override
        public Enumeration<String> getKeys()
        {
            Collection<String> keys = bundles.stream()
                                             .filter(delegate -> delegate != null)
                                             .flatMap(delegate -> Collections.list(delegate.getKeys()).stream())
                                             .collect(Collectors.toList());

            return Collections.enumeration(keys);
        }
    }

    class PluginBundleControl extends Control
    {

        private final String baseName;
        private final String[] dependentBaseNames;

        public PluginBundleControl(String baseName, String... dependentBaseNames)
        {
            this.baseName = baseName;
            this.dependentBaseNames = dependentBaseNames;
        }

        public String getBaseName()
        {
            return this.baseName;
        }

        @Override
        public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload)
            throws IllegalAccessException, InstantiationException, IOException
        {
            List<ResourceBundle> delegates = Arrays.stream(this.dependentBaseNames)
                                                   .filter(currentBaseName -> currentBaseName != null && !"".equals(currentBaseName.trim()))
                                                   .map(currentBaseName -> ResourceBundle.getBundle(currentBaseName, locale))
                                                   .collect(Collectors.toList());

            return new PluginResourceBundle(delegates);
        }
    }
}
