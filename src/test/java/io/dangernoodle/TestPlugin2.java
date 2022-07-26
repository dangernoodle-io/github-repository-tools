package io.dangernoodle;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.google.inject.AbstractModule;
import com.google.inject.Module;

import io.dangernoodle.grt.Plugin;


public class TestPlugin2 implements Plugin
{
    @Override
    public Collection<Module> getModules()
    {
        return List.of(new AbstractModule()
        {
            // empty
        });
    }

    @Override
    public Optional<String> getResourceBundle()
    {
        return Optional.empty();
    }

    @Override
    public Optional<String> getPluginSchema()
    {
        return Optional.empty();
    }
}
