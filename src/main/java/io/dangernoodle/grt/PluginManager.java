package io.dangernoodle.grt;

import java.util.Map;
import java.util.Optional;

import io.dangernoodle.grt.util.JsonTransformer.JsonObject;


/**
 * @since 0.9.0
 */
public interface PluginManager
{
    JsonObject getDefaultPluginConfiguration(String name);

    /**
     * @since 0.10.0
     */
    Map<String, Optional<String>> getPluginSchemas();
}
