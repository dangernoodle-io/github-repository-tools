package io.dangernoodle.grt.ext.statuschecks;

import java.util.Collection;

import io.dangernoodle.grt.Repository;


/**
 * @since 0.1.0
 */
public interface StatusCheckProvider
{
    /**
     * Return the required status checks for the given branch name
     * 
     * @return status checks or an <code>empty</code> collection
     */
    Collection<String> getRequiredStatusChecks(String branch, Repository repository);
}
