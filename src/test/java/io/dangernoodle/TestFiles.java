package io.dangernoodle;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;


public enum TestFiles
{
    branchProtectionOnly,
    credentials,
    mockRepository,
    noBranches,
    nullBranchProtection,
    nullBranchProtections,
    nullWorkflow,
    requireStatusChecks;

    private static final List<String> dirs;

    static
    {
        dirs = Arrays.asList("/test-files", "/repositories");
    }

    public final String jsonFile;

    private TestFiles()
    {
        this.jsonFile = this.toString();
    }

    public File getFile()
    {
        return new File(find(file -> getClass().getResource(file)).getFile());
    }

    public InputStream getInputStream()
    {
        return find(file -> getClass().getResourceAsStream(file));
    }

    public String loadJson()
    {
        try (Scanner scanner = new Scanner(getInputStream(), "UTF-8"))
        {
            return scanner.useDelimiter("\\Z").next();
        }
    }

    
    public String toJson()
    {
        return loadJson();
    }

    private <T> T find(Function<String, T> function)
    {
        return dirs.stream()
                   .map(dir -> String.format("%s/%s.json", dir, jsonFile))
                   .map(function::apply)
                   .filter(file -> file != null)
                   .findFirst()
                   .orElseThrow(() -> new RuntimeException("failed to find json file for " + this));
    }
}
