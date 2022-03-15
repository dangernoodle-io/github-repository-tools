package io.dangernoodle.grt.credentials;

import static io.dangernoodle.grt.Constants.GITHUB_APP;

import java.util.Map;
import java.util.Optional;

import io.dangernoodle.grt.Credentials;
import io.dangernoodle.grt.util.JsonTransformer.JsonObject;
import io.dangernoodle.grt.util.JsonTransformer.JsonObject.Deserializer;


/**
 * Provides credentials stored in a <code>JsonObject</code>.
 * <p>
 * Credentials found in a <code>credentials.json</code> stored under the supplied root directory are automatically
 * loaded upon startup.
 * 
 * <pre>
 * {
 *     "github": "oath-token",
 *     "service": "some-token",
 *     "nameValue": {
 *         "name": "value"
 *     }
 * }
 * </pre>
 * 
 * This provider is intended to be run last when used in conjunction with the <code>ChainedCredentials</code> provider.
 * </p>
 */
public class JsonCredentials implements Credentials
{
    private final JsonObject json;

    public JsonCredentials(JsonObject json)
    {
        this.json = json;
    }

    @Override
    public String getCredentials(String key)
    {
        return json.getString(key);
    }

    @Override
    public Map<String, Object> getNameValue(String key)
    {
        // github apps aren't supported in via json credentials
        return Optional.of(key)
                       .filter(k -> !GITHUB_APP.equals(k))
                       .map(this::deserialize)
                       .orElse(null);
    }

    private Map<String, Object> deserialize(String key)
    {
        return json.getMap(key, new Deserializer<>()
        {
            @Override
            public Object apply(String value)
            {
                return value;
            }
        });
    }
}
