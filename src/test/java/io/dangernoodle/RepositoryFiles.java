package io.dangernoodle;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import io.dangernoodle.grt.util.JsonTransformer;
import io.dangernoodle.grt.util.JsonTransformer.JsonObject;


public enum RepositoryFiles
{
    branchProtectionOnly,
    credentials,
    emptyCredentials,
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

    public static Path dummyPemPath() throws URISyntaxException
    {
        return Path.of(RepositoryFiles.class.getResource("/dummy-key.pem").toURI());
    }
    
    public final String jsonFile;

    private RepositoryFiles()
    {
        this.jsonFile = this.toString();
    }

    public Path getPath() throws URISyntaxException
    {
        return Path.of(find().toURI());
    }

    public JsonObject toJsonObject() throws IOException, URISyntaxException
    {
        return new JsonTransformer().deserialize(getPath());
    }

    private URL find()
    {
        return dirs.stream()
                   .map(dir -> String.format("%s/%s.json", dir, jsonFile))
                   .map(path -> getClass().getResource(path))
                   .filter(file -> file != null)
                   .findFirst()
                   .orElseThrow(() -> new RuntimeException("failed to find json file for " + this));
    }
}
