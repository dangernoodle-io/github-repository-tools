package io.dangernoodle;

import java.util.Collection;
import java.util.List;

import com.google.inject.AbstractModule;
import com.google.inject.Module;

import io.dangernoodle.grt.Plugin;


public class TestPlugin1 implements Plugin
{
    @Override
    public Collection<Module> getModules()
    {
        return List.of(new AbstractModule()
        {
            // empty
        });
    }
}
