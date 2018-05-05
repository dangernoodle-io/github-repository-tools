package io.dangernoodle.grt.internal;

import java.io.IOException;
import java.io.InputStream;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.dangernoodle.grt.json.JsonSchemaValidator;
import io.dangernoodle.grt.json.JsonValidationException;


public class EveritSchemaValidator implements JsonSchemaValidator
{
    private static final Logger logger = LoggerFactory.getLogger(EveritSchemaValidator.class);

    private final Schema schema;

    public EveritSchemaValidator(InputStreamSupplier supplier) throws IOException
    {
        schema = SchemaLoader.load(loadJson(supplier));
    }

    @Override
    public void validate(InputStreamSupplier supplier) throws IOException
    {
        try
        {
            schema.validate(loadJson(supplier));
        }
        catch (ValidationException e)
        {
            logViolations(e);
            throw new JsonValidationException(e);
        }
    }

    private void logViolations(ValidationException exception)
    {
        logger.error("{}", exception.getMessage());
        exception.getCausingExceptions()
                 .stream()
                 .forEach(this::logViolations);
    }

    private JSONObject loadJson(InputStreamSupplier supplier) throws IOException
    {
        try (InputStream inputStream = supplier.get())
        {
            return new JSONObject(new JSONTokener(inputStream));
        }
    }

    @ApplicationScoped
    public static class EveritSchemaValidatorProducer
    {
        @Produces
        @ApplicationScoped
        public JsonSchemaValidator get() throws IOException
        {
            return new EveritSchemaValidator(() -> getClass().getResourceAsStream("/repository-schema.json"));
        }
    }
}
