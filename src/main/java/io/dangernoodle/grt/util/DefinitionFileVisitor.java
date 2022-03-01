package io.dangernoodle.grt.util;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;


public class DefinitionFileVisitor extends SimpleFileVisitor<Path>
{
    private int count;

    private final Handler handler;

    private final PathMatcher matcher;

    public DefinitionFileVisitor(String match, Handler handler)
    {
        this.handler = handler;
        this.matcher = FileSystems.getDefault().getPathMatcher("glob:**/" + match + ".json");
    }

    public boolean matched()
    {
        return count > 0;
    }

    @Override
    public FileVisitResult visitFile(Path path, BasicFileAttributes attr) throws IOException
    {
        if (matcher.matches(path))
        {
            count++;
            handler.accept(path);
        }

        return FileVisitResult.CONTINUE;
    }

    public static interface Handler
    {
        void accept(Path definition) throws IOException;
    }
}
