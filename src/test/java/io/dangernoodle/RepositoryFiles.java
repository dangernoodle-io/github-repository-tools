package io.dangernoodle;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import io.dangernoodle.grt.utils.JsonTransformer;
import io.dangernoodle.grt.utils.JsonTransformer.JsonObject;


public enum RepositoryFiles
{
    branchProtectionOnly,
    credentials,
    mockRepository,
    noBranches,
    nullBranchProtection,
    nullWorkflow,
    requireStatusChecks;

    private static final List<String> dirs;

    static
    {
        dirs = Arrays.asList("/test-files", "/repositories");
    }

    public final String jsonFile;

    private RepositoryFiles()
    {
        this.jsonFile = this.toString();
    }

    public File getFile()
    {
        return new File(find(file -> getClass().getResource(file)).getFile());
    }

    public JsonObject toJsonObject() throws IOException
    {
        return new JsonTransformer().deserialize(getFile());
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
