package io.dangernoodle.grt;

import java.util.Collection;
import java.util.Collections;


/**
 * @since 0.9.0
 */
public interface StatusCheck
{
    public static final StatusCheck NULL = new StatusCheck()
    {
        @Override
        public Collection<String> getCommands()
        {
            return Collections.emptyList();
        }

        @Override
        public Collection<String> getRequiredChecks(String branch, Repository repository)
        {
            return Collections.emptyList();
        }
    };

    /**
     * Return the commands which will utilize this status check
     *
     * @return command names or an <code>empty</code> collection
     */
    Collection<String> getCommands();

    /**
     * Return the required status checks for the given branch name
     * 
     * @return status checks or an <code>empty</code> collection
     */
    Collection<String> getRequiredChecks(String branch, Repository repository);
}
