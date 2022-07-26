package io.dangernoodle.grt.util;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.Iterator;
import java.util.stream.Stream;

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
        try (Stream<Path> stream = getDefinitions(root))
        {
            Iterator<Path> iterator = stream.filter(matcher::matches)
                                            .sorted()
                                            .iterator();

            int count = 0;
            while (iterator.hasNext())
            {
                count++;
                handler.accept(iterator.next());
            }

            return count;
        }
    }

    // visible for testing
    Stream<Path> getDefinitions(Path root) throws IOException
    {
        return Files.walk(root);
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
