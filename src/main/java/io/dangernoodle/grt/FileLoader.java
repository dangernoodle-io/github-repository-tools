package io.dangernoodle.grt;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;


public class FileLoader
{
    private final String repoDir;

    private final String root;

    public FileLoader(String repoDir)
    {
        this.root = repoDir;
        this.repoDir = root + File.separator + "repositories";
    }

    public File loadCredentials() throws IOException
    {
        // depth = 1 for the credentials file, it should be at top level of root directory
        return findFile(root, 1, "credentials");
    }

    public File loadRepositoryDefaults() throws IOException
    {
        // depth = 1 for the configuration file, it should be at top level of root directory
        return findFile(root, 1, "github-repository-tools");
    }

    public File loadRepository(String name) throws IOException
    {
        // depth = 10 is somewhat arbitrary - can be increased if there is ever a need
        // automatically swap '.' for '-' in the repository name
        return findFile(repoDir, 10, name.replace('.', '-'));
    }

    private File findFile(String root, int depth, String name) throws IOException
    {
        List<Path> files = Files.find(Paths.get(root), depth, (path, attrs) -> {
            return path.getFileName().toString().equals(name + ".json");
        }).collect(Collectors.toList());

        if (files.size() == 0)
        {
            throw new FileNotFoundException("failed to find repository file [" + name + "]");
        }

        if (files.size() > 1)
        {
            throw new FileAlreadyExistsException("multiple repsository files named [" + name + "] found");
        }

        return files.get(0).toFile();
    }
}
