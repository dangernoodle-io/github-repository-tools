package io.dangernoodle.grt.workflows;

import static io.dangernoodle.grt.Constants.VALIDATE;
import static io.dangernoodle.grt.utils.GithubRepositoryToolsUtils.toHex;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.dangernoodle.grt.Workflow;
import io.dangernoodle.grt.utils.JsonTransformer;
import io.dangernoodle.grt.utils.JsonValidationException;


public class ValidatorWorkflow implements Workflow<Path>
{
    private final MessageDigest digest;

    private final Map<Path, Exception> errors;

    private final JsonTransformer jsonTransformer;

    private final Map<String, List<Path>> md5sums;

    private final Map<String, List<Path>> names;

    private Path configuration;

    private boolean detailedReport;

    public ValidatorWorkflow(Path configuration, JsonTransformer jsonTransformer, boolean detailedReport)
    {
        this.configuration = configuration;
        this.detailedReport = detailedReport;
        this.jsonTransformer = jsonTransformer;

        this.errors = new HashMap<>();
        this.md5sums = new HashMap<>();
        this.names = new HashMap<>();

        this.digest = createMessageDigest();
    }

    @Override
    public void execute(Path path, Context context) throws Exception
    {
        String name = path.toFile().getName();
        names.computeIfAbsent(name, n -> new ArrayList<>()).add(path);

        try (DigestInputStream dis = new DigestInputStream(Files.newInputStream(path), digest))
        {
            jsonTransformer.validate(dis);
            md5sums.computeIfAbsent(toHex(digest.digest()), k -> new ArrayList<>()).add(path);
        }
        catch (JsonValidationException e)
        {
            errors.put(path, e);
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
//        if (!hasErrors())
//        {
//           
//        }
        
        
        
    }

    @Override
    public void preExecution()
    {
        try
        {
            execute(configuration, null);
        }
        catch (JsonValidationException e)
        {
            errors.put(configuration, e);
        }
        catch (Exception e)
        {
            throw e instanceof RuntimeException ? (RuntimeException) e : new RuntimeException(e);
        }
    }

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
