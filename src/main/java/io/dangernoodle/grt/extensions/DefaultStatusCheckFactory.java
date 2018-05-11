package io.dangernoodle.grt.extensions;

import java.util.Collection;

import io.dangernoodle.grt.Repository;


public class DefaultStatusCheckFactory implements StatusCheckFactory
{
    @Override
    public Collection<String> getRequiredStatusChecks(String branch, Repository repository)
    {
        return repository.getSettings()
                         .getBranches()
                         .getProtection(branch)
                         .getRequiredChecks()
                         .getContexts();
    }
}
