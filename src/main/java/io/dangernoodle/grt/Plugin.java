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

    default Optional<String> getResourceBundle()
    {
        return Optional.of(getClass().getSimpleName());
    }
}
