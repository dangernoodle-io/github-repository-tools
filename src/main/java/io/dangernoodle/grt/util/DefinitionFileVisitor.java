package io.dangernoodle.grt.util;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @since 0.9.0
 */
public class DefinitionFileVisitor
{
    private static final Logger logger = LoggerFactory.getLogger(DefinitionFileVisitor.class);

    private final PathMatcher matcher;

    public DefinitionFileVisitor(String match)
    {
        this.matcher = pathMatcher(match);
    }

    public int visit(Path root, Handler handler) throws Exception
    {
        int count = 0;
        Iterator<Path> iterator = iterator(root);

        while (iterator.hasNext())
        {
            count++;
            handler.accept(iterator.next());
        }

        return count;
    }

    // visible for testing
    Iterator<Path> iterator(Path root) throws IOException
    {
        return Files.walk(root)
                    .filter(matcher::matches)
                    .sorted()
                    .iterator();
    }

    // visible for testing
    static PathMatcher pathMatcher(String match)
    {
        String pattern = String.format("glob:**/%s.json", match);
        logger.debug("path matcher pattern: [{}]", pattern);

        return FileSystems.getDefault().getPathMatcher(pattern);
    }

    /**
     * @since 0.9.0
     */
    public static interface Handler
    {
        void accept(Path definition) throws Exception;
    }
}
