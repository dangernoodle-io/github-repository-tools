package io.dangernoodle.grt.internal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

    /**
     * @since 0.5.0
     */
    public Collection<File> loadRepositories(String subDir) throws IOException
    {
        Set<File> seen = new HashSet<>();
        Set<File> dups = new HashSet<>();

        Path start = Paths.get(repoDir, subDir == null ? "" : subDir);
        Collection<File> found = Files.walk(start, Integer.MAX_VALUE)
                                      .map(Path::toFile)
                                      .filter(File::isFile)
                                      .filter(file -> file.getName().endsWith(".json"))
                                      .map(file -> {
                                          if (seen.contains(file))
                                          {
                                              dups.add(file);
                                          }

                                          seen.add(file);
                                          return file;
                                      })
                                      .sorted()
                                      .collect(Collectors.toList());

        if (!dups.isEmpty())
        {
            throw new FileAlreadyExistsException("multiple repository files found for: " + dups);
        }

        return found;
    }

    public File loadRepository(String name) throws IOException
    {
        // automatically swap '.' for '-' in the repository name
        return findFile(repoDir, Integer.MAX_VALUE, name.replace('.', '-'));
    }

    public File loadRepositoryDefaults() throws IOException
    {
        // depth = 1 for the configuration file, it should be at top level of root directory
        return findFile(root, 1, "github-repository-tools");
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
            throw new FileAlreadyExistsException("multiple repository files named [" + name + "] found");
        }

        return files.get(0).toFile();
    }
}
