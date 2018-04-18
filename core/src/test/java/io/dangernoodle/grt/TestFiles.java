package io.dangernoodle.grt;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import io.dangernoodle.grt.json.DefaultJsonTransformer;


public enum TestFiles
{
    mockRepository;

    private static final List<String> dirs;

    static
    {
        dirs = Arrays.asList("/test-files");
    }

    public final String jsonFile;

    private TestFiles()
    {
        this.jsonFile = this.toString();
    }

    public String loadJson()
    {
        try (Scanner scanner = new Scanner(getInputStream(), "UTF-8"))
        {
            return scanner.useDelimiter("\\Z").next();
        }
    }
    
    public <T> T parseIntoObject(Class<T> clazz)
    {
        return DefaultJsonTransformer.transformer.deserialize(toJson(), clazz);
    }

    public String toJson()
    {
        return loadJson();
    }

    private InputStream getInputStream()
    {
        return dirs.stream()
                   .map(dir -> String.format("%s/%s.json", dir, jsonFile))
                   .map(file -> getClass().getResourceAsStream(file))
                   .filter(stream -> stream != null)
                   .findFirst()
                   .orElseThrow(() -> new RuntimeException("failed to find json file for " + this));
    }
}
