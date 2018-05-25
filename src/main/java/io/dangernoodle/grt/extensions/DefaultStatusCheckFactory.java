package io.dangernoodle.grt.extensions;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import io.dangernoodle.grt.Repository;


/**
 * Default status check factory.
 * <p>
 * Returns the <code>contexts</code> field from the <code>protections</code> section of the repository definition for
 * the given branch.
 * </p>
 * 
 * @since 0.1.0
 */
public class DefaultStatusCheckFactory implements StatusCheckFactory
{
    @Override
    public Collection<String> getRequiredStatusChecks(String branch, Repository repository)
    {
        return Optional.ofNullable(repository.getSettings()
                                             .getBranches()
                                             .getProtection(branch)
                                             .getRequiredChecks()
                                             .getContexts())
                       .orElse(Collections.emptyList());
    }
}
