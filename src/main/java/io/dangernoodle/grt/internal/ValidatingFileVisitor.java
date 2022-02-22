package io.dangernoodle.grt.internal;

import static io.dangernoodle.grt.utils.GithubRepositoryToolsUtils.toHex;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.everit.json.schema.ValidationException;

import io.dangernoodle.grt.utils.JsonTransformer;
import io.dangernoodle.grt.utils.JsonValidationException;


public class ValidatingFileVisitor extends SimpleFileVisitor<Path>
{
    private final MessageDigest digest;

    private final Map<Path, JsonValidationException> errors;

    private final JsonTransformer jsonTransformer;

    private final PathMatcher matcher;

    private final Map<String, List<Path>> md5sums;

    private final Map<String, List<Path>> names;

    public ValidatingFileVisitor(JsonTransformer jsonTransformer)
    {
        this.jsonTransformer = jsonTransformer;

        this.errors = new HashMap<>();
        this.md5sums = new HashMap<>();
        this.names = new HashMap<>();

        this.digest = createMessageDigest();
        this.matcher = FileSystems.getDefault().getPathMatcher("glob:**/*.json");
    }

    public boolean hasErrors()
    {
        return hasDuplicates(names) || hasDuplicates(md5sums) || !errors.isEmpty();
    }

    private void logViolations(ValidationException exception)
    {
        // logger.error("{}", exception.getMessage());
        System.out.println(exception.getMessage());
        exception.getCausingExceptions()
                 .stream()
                 .forEach(this::logViolations);
    }

    public void report()
    {
        // filterDups(md5su

        if (!errors.isEmpty())
        {
            System.out.println("the following files have json schema errors:");

            errors.forEach((k, v) -> {
                System.out.println(k);

                v.getValidationErrors()
                 .forEach(System.out::println);
            });

        }

        // filterDups(names).forEach((k, v) -> System.out.printn

        // md5sums.forEach((k, v) -> System.out.println(k + " - " + v));

        // errors.forEach((k, v) -> System.out.println(k + " - " + v));
    }

    @Override
    public FileVisitResult visitFile(Path path, BasicFileAttributes attr) throws IOException
    {
        if (matcher.matches(path))
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

        return FileVisitResult.CONTINUE;
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

    private Stream<List<Path>> filterDups(Map<String, List<Path>> map)
    {
        return map.values()
                  .stream()
                  .filter(values -> values.size() > 1);
    }

    private boolean hasDuplicates(Map<String, List<Path>> map)
    {
        return filterDups(map).findAny()
                              .isPresent();
    }
}
