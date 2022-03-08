package io.dangernoodle.grt.statuscheck;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import io.dangernoodle.grt.Repository;
import io.dangernoodle.grt.StatusCheck;


public class CommandStatusCheck implements StatusCheck
{
    private final String command;

    private final Collection<StatusCheck> statusChecks;

    public CommandStatusCheck(String command, Collection<StatusCheck> statusChecks)
    {
        this.command = command;
        this.statusChecks = statusChecks;
    }

    public CommandStatusCheck(String command, StatusCheck... statusChecks)
    {
        this(command, List.of(statusChecks));
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
                           .filter(check -> check.getCommands().contains(command))
                           .flatMap(check -> check.getRequiredChecks(branch, repository).stream())
                           .collect(Collectors.toList());
    }
}
