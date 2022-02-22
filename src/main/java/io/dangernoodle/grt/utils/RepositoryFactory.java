package io.dangernoodle.grt.utils;

import java.io.IOException;
import java.nio.file.Path;

import io.dangernoodle.grt.Repository;

/**
 * @since 0.9.0
 */
public class RepositoryFactory
{
    private static final String CONFIG_FILE = "github-repository-tools.json";

    private final Repository defaults;

    private final RepositoryMerger merger;

    private final Path root;

    private final JsonTransformer transformer;

    public RepositoryFactory(JsonTransformer transformer, Path root) throws IOException
    {
        this.root = root;
        this.transformer = transformer;

        this.merger = createRepositoryMerger();
        this.defaults = createRepository(root.resolve(CONFIG_FILE));
    }

    public RepositoryBuilder createBuilder()
    {
        return new RepositoryBuilder(transformer)
        {
            @Override
            public Repository build()
            {
                return merger.merge(super.build());
            }
        };
    }

    public Path getDefinitionsRoot()
    {
        return resolveDefinitionsRoot(root);
    }

    public Repository load(Path definition) throws IOException, IllegalStateException
    {
        return merger.merge(createRepository(definition), defaults);
    }

    RepositoryMerger createRepositoryMerger()
    {
        return new RepositoryMerger(transformer);
    }

    private Repository createRepository(Path path) throws IOException
    {
        return new Repository(transformer.deserialize(path.toFile()));
    }

    public static Path resolveDefinitionsRoot(Path path)
    {
        return path.resolve("repositories");
    }
}
