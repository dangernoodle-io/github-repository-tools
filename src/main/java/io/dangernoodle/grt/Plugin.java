package io.dangernoodle.grt;

import java.util.Collection;
import java.util.Collections;

import com.google.inject.Module;


/**
 * @since 0.9.0
 */
public interface Plugin
{
    Collection<Module> getModules();

    Collection<Class<? extends Command>> getCommands();

    default Collection<Module> getOverrides()
    {
        return Collections.emptyList();
    }
}
