package io.dangernoodle.grt.statuscheck;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import io.dangernoodle.grt.Repository;
import io.dangernoodle.grt.StatusCheck;


public class CompositeStatusCheck implements StatusCheck
{
    private final Collection<StatusCheck> statusChecks;

    public CompositeStatusCheck(Collection<StatusCheck> statusChecks)
    {
        this.statusChecks = statusChecks;
    }

    public CompositeStatusCheck(StatusCheck... statusChecks)
    {
        this(List.of(statusChecks));
    }

    @Override
    public Collection<String> getCommands()
    {
        // this class is a delegate only
        throw new UnsupportedOperationException("unsupported");
    }
    
    @Override
    public Collection<String> getRequiredChecks(String branch, Repository repository)
    {
        return statusChecks.stream()
                           .map(provider -> provider.getRequiredChecks(branch, repository))
                           .flatMap(Collection::stream)
                           .collect(Collectors.toSet());
    }
}
