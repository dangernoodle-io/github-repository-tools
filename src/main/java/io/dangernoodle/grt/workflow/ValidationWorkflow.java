package io.dangernoodle.grt.workflow;

import static io.dangernoodle.grt.Constants.VALIDATE;
import static io.dangernoodle.grt.util.GithubRepositoryToolsUtils.toHex;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.dangernoodle.grt.Workflow;
import io.dangernoodle.grt.util.JsonTransformer;
import io.dangernoodle.grt.util.JsonValidationException;
import io.dangernoodle.grt.util.JsonValidationReporter;
import io.dangernoodle.grt.util.SilentException;


public class ValidationWorkflow implements Workflow<Path>
{
    private static final Logger logger = LoggerFactory.getLogger(ValidationWorkflow.class);

    private Path configuration;

    private final MessageDigest digest;

    private final JsonTransformer jsonTransformer;

    private final JsonValidationReporter reporter;

    public ValidationWorkflow(Path configuration, JsonTransformer jsonTransformer, boolean detailedReport)
    {
        this.configuration = configuration;
        this.jsonTransformer = jsonTransformer;

        this.digest = createMessageDigest();
        this.reporter = new JsonValidationReporter(detailedReport);
    }

    @Override
    public void execute(Path path, Context context) throws Exception
    {
        reporter.add(path);

        try (DigestInputStream dis = new DigestInputStream(Files.newInputStream(path), digest))
        {
            jsonTransformer.validate(dis);
            reporter.add(path, toHex(digest.digest()));
        }
        catch (JsonValidationException e)
        {
            reporter.add(path, e);
        }
        finally
        {
            digest.reset();
        }
    }

    @Override
    public String getName()
    {
        return VALIDATE;
    }

    @Override
    public void postExecution()
    {
        boolean hasErrors = reporter.report();
        logger.info("definition validation complete - {} errors found!", reporter.errorCount());

        if (hasErrors && !reporter.isDetailed())
        {
            logger.info("please run the 'validate' command to obtain further details");
            throw new SilentException("definition files contain validation errors");
        }
    }

    @Override
    public void preExecution()
    {
        logger.info("starting definition validation");

        try
        {
            execute(configuration, null);
        }
        catch (JsonValidationException e)
        {
            reporter.add(configuration, e);
        }
        catch (Exception e)
        {
            // if any other exception occurs here, abort entirely
            throw e instanceof RuntimeException ? (RuntimeException) e : new RuntimeException(e);
        }
    }

    @SuppressWarnings("WEAK_MESSAGE_DIGEST_MD5")
    MessageDigest createMessageDigest()
    {
        try
        {
            return MessageDigest.getInstance("MD5");
        }
        catch (NoSuchAlgorithmException e)
        {
            // shouldn't happen...
            throw new RuntimeException(e);
        }
    }
}
