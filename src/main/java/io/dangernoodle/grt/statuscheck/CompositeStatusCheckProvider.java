package io.dangernoodle.grt.statuscheck;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import io.dangernoodle.grt.Repository;
import io.dangernoodle.grt.StatusCheck;


public class CompositeStatusCheckProvider implements StatusCheck
{
    private final Collection<StatusCheck> providers;

    public CompositeStatusCheckProvider(StatusCheck... providers)
    {
        this.providers = Arrays.asList(providers);
    }

    @Override
    public Collection<String> getRequiredChecks(String branch, Repository repository)
    {
        return providers.stream()
                        .map(provider -> provider.getRequiredChecks(branch, repository))
                        .flatMap(Collection::stream)
                        .collect(Collectors.toSet());
    }
}
