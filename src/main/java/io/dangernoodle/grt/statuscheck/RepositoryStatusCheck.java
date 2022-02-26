package io.dangernoodle.grt.statuscheck;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import io.dangernoodle.grt.Repository;
import io.dangernoodle.grt.StatusCheck;


/**
 * Default status check factory.
 * <p>
 * Returns the <code>contexts</code> field from the <code>protections</code> section of the repository definition for
 * the given branch.
 * </p>
 * 
 * @since 0.1.0
 */
public class RepositoryStatusCheck implements StatusCheck
{
    @Override
    public Collection<String> getRequiredChecks(String branch, Repository repository)
    {
        return Optional.ofNullable(repository.getSettings()
                                             .getBranches()
                                             .getProtection(branch)
                                             .getRequiredChecks()
                                             .getContexts())
                       .orElse(Collections.emptyList());
    }
}
