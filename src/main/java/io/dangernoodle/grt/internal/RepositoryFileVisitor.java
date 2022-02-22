package io.dangernoodle.grt.internal;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;


public class RepositoryFileVisitor extends SimpleFileVisitor<Path>
{
    private final PathMatcher matcher;

    private final Handler handler;

    private int count;

    public RepositoryFileVisitor(String match, Handler handler)
    {
        this.handler = handler;
        this.matcher = FileSystems.getDefault().getPathMatcher("glob:**/" + match + ".json");
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

    public boolean matched()
    {
        return count > 0;
    }

    public static interface Handler
    {
        void accept(Path definition) throws IOException;
    }
}
