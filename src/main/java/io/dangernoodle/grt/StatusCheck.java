package io.dangernoodle.grt;

import java.util.Collection;
import java.util.Collections;


/**
 * @since 0.9.0
 */
public interface StatusCheck
{
    /**
     * Return the commands which will utilize this status check
     *
     * @return command names or an <code>empty</code> collection
     */
    default Collection<String> getCommands()
    {
        return Collections.emptyList();
    }

    /**
     * Return the required status checks for the given branch name
     * 
     * @return status checks or an <code>empty</code> collection
     */
    Collection<String> getRequiredChecks(String branch, Repository repository);
}
