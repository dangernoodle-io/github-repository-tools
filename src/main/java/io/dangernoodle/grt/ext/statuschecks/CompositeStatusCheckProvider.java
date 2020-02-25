package io.dangernoodle.grt.ext.statuschecks;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import io.dangernoodle.grt.Repository;


public class CompositeStatusCheckProvider implements StatusCheckProvider
{
    private final Collection<StatusCheckProvider> providers;

    public CompositeStatusCheckProvider(StatusCheckProvider... providers)
    {
        this.providers = Arrays.asList(providers);
    }

    @Override
    public Collection<String> getRequiredStatusChecks(String branch, Repository repository)
    {
        return providers.stream()
                        .map(provider -> provider.getRequiredStatusChecks(branch, repository))
                        .flatMap(Collection::stream)
                        .collect(Collectors.toSet());
    }
}
