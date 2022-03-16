package io.dangernoodle.grt;

import io.dangernoodle.grt.util.JsonTransformer.JsonObject;


/**
 * @since 0.9.0
 */
public interface PluginManager
{
    JsonObject getDefaultPluginConfiguration(String name);
}
