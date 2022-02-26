package io.dangernoodle.grt.util;

import io.dangernoodle.grt.util.JsonTransformer.JsonObject;


/**
 * @since 0.9.0
 */
public interface PluginsManager
{
    JsonObject getDefaultConfiguration(String name);
}
