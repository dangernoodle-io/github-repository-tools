package io.dangernoodle.grt;

import java.util.Collection;


/**
 * @since 0.9.0
 */
public interface StatusCheck
{
    /**
     * Return the required status checks for the given branch name
     * 
     * @return status checks or an <code>empty</code> collection
     */
    Collection<String> getRequiredChecks(String branch, Repository repository);
}
