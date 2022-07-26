package io.dangernoodle.grt;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import com.google.inject.Module;


/**
 * @since 0.9.0
 */
public interface Plugin
{
    default Collection<Class<? extends Command>> getCommands()
    {
        return Collections.emptyList();
    }

    Collection<Module> getModules();

    default Collection<Module> getOverrides()
    {
        return Collections.emptyList();
    }

    /**
     * Returns the <code>name</code> of the plugin as defined in the <code>plugins</code> section of the definition file.
     * 
     * @since 0.10.0
     */
    default String getName()
    {
        return getClass().getSimpleName();
    }

    /**
     * Returns the name of the resource bundle for the plugin, using <code>getClass().getSimpleName()</code> as the
     * default.
     * <p>
     * If you do not wish to provide a resource bundle, you may return an empty <code>Optional</code>.
     * </p>
     * 
     * @since 0.10.0
     */
    default Optional<String> getResourceBundle()
    {
        return Optional.of(getClass().getSimpleName());
    }

    /**
     * Returns the name of the json schema for the plugin, using <code>getClass().getSimpleName()</code> as the default.
     * <p>
     * If you do not wish to provide a schema you may return an empty <code>Optional</code>.
     * </p>
     * 
     * @since 0.10.0
     */
    default Optional<String> getPluginSchema()
    {
        return Optional.of(String.format("/%s.json", getClass().getSimpleName()));
    }
}
