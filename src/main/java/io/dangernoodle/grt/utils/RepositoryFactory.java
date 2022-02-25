package io.dangernoodle.grt.utils;

import java.io.IOException;
import java.nio.file.Path;

import io.dangernoodle.grt.Repository;


/**
 * @since 0.9.0
 */
public class RepositoryFactory
{
    private final Repository defaults;

    private final RepositoryMerger merger;

    private final JsonTransformer transformer;

    public RepositoryFactory(Path configuration, JsonTransformer transformer) throws IOException
    {
        this.transformer = transformer;

        this.merger = createRepositoryMerger();
        this.defaults = createRepository(configuration);
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
}
