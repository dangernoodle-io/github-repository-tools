package io.dangernoodle.grt.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.ServiceLoader;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.util.Modules;

import io.dangernoodle.grt.Command;
import io.dangernoodle.grt.Plugin;
import io.dangernoodle.grt.repository.RepositoryFactory;
import io.dangernoodle.grt.util.JsonTransformer.JsonObject;
import io.dangernoodle.grt.util.PluginsManager;


/**
 * @since 0.9.0
 */
public class Bootstrapper implements PluginsManager
{
    private final Injector injector;

    private final Collection<Plugin> plugins;

    public Bootstrapper()
    {
        this.plugins = loadPlugins();
        this.injector = createInjector();
    }

    public Collection<Class<? extends Command>> getCommands()
    {
        return collect(Plugin::getCommands);
    }

    @Override
    public JsonObject getDefaultPluginConfiguration(String name)
    {
        return injector.getInstance(RepositoryFactory.class)
                       .getDefaults()
                       .getPlugin(name);
    }

    public Injector getInjector()
    {
        return injector;
    }

    public ResourceBundle getResourceBundle()
    {
        return PluginsBundle.merge(getResourceBundles());
    }

    // visible for testing
    Plugin createCorePlugin()
    {
        return new CorePlugin();
    }

    private <T> Collection<T> collect(Function<Plugin, Collection<T>> function)
    {
        return collect(function, Collections.emptyList());
    }

    private <T> Collection<T> collect(Function<Plugin, Collection<T>> function, Collection<T> additional)
    {
        Stream<T> stream = plugins.stream()
                                  .map(function)
                                  .flatMap(Collection::stream);

        return Stream.concat(stream, additional.stream())
                     .collect(Collectors.toList());
    }

    private Injector createInjector()
    {
        Module module = Modules.override(collect(Plugin::getModules, List.of(createPluginsModule())))
                               .with(collect(Plugin::getOverrides));

        return Guice.createInjector(module);
    }

    private Module createPluginsModule()
    {
        return new AbstractModule()
        {
            @Provides
            public PluginsManager plugins()
            {
                return Bootstrapper.this;
            }
        };
    }

    private List<String> getResourceBundles()
    {
        return plugins.stream()
                      .map(Plugin::getResourceBundle)
                      .filter(Optional::isPresent)
                      .map(Optional::get)
                      .collect(Collectors.toList());
    }

    private Collection<Plugin> loadPlugins()
    {
        // the 'core' plugin is always first
        ArrayList<Plugin> plugins = new ArrayList<>();
        plugins.add(createCorePlugin());

        ServiceLoader.load(Plugin.class)
                     .stream()
                     .map(p -> p.get())
                     .forEach(plugins::add);

        return plugins;
    }
}
