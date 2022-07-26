package io.dangernoodle.grt.util;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JsonValidationReporter
{
    private static final Logger logger = LoggerFactory.getLogger(JsonValidationReporter.class);

    private final boolean detailed;

    private final Map<Path, JsonValidationException> jsonErrors;

    private final Map<String, List<Path>> md5sums;

    private final Map<String, List<Path>> names;

    public JsonValidationReporter(boolean detailed)
    {
        this.detailed = detailed;

        this.names = new LinkedHashMap<>();
        this.md5sums = new LinkedHashMap<>();
        this.jsonErrors = new LinkedHashMap<>();
    }

    public void add(Path path)
    {
        names.computeIfAbsent(path.toFile().getName(), n -> new ArrayList<>()).add(path);
    }

    public void add(Path path, JsonValidationException e)
    {
        jsonErrors.put(path, e);
    }

    public void add(Path path, String md5sum)
    {
        md5sums.computeIfAbsent(md5sum, k -> new ArrayList<>()).add(path);
    }

    public boolean hasErrors()
    {
        return errorCount() > 0 || hasDuplicates(names) || hasDuplicates(md5sums);
    }
    
    public boolean isDetailed()
    {
        return detailed;
    }

    public boolean report()
    {
        if (detailed)
        {
            names.forEach(this::logDuplicateNames);

            md5sums.forEach(this::logDuplicateContents);
            jsonErrors.forEach(this::logJsonErrors);
        }

        return hasErrors();
    }

    public long errorCount()
    {
        return paths(names).count() + paths(md5sums).count() + jsonErrors.size();
    }

    private Stream<List<Path>> paths(Map<String, List<Path>> map)
    {
        return map.values()
                  .stream()
                  .filter(list -> list.size() > 1);
    }

    private boolean hasDuplicates(Map<String, List<Path>> map)
    {
        return map.values()
                  .stream()
                  .filter(list -> list.size() > 1)
                  .count() > 0;
    }

    private void logDuplicateContents(String md5sum, List<Path> paths)
    {
        if (paths.size() > 1)
        {
            logger.error("found multiple definition files with md5sum [{}]: ", md5sum);
            paths.forEach(path -> logger.error("\t{}", path));
        }
    }

    private void logDuplicateNames(String name, List<Path> paths)
    {
        if (paths.size() > 1)
        {
            logger.error("found multiple defintion files with name [{}]: ", name);
            paths.forEach(path -> logger.error("\t{}", path));
        }
    }

    private void logJsonErrors(Path path, JsonValidationException exception)
    {
        logger.error("definition [{}] contains schema errors", path);
        exception.getValidationErrors()
                 .forEach(message -> logger.error("\t{}", message));
    }
}
