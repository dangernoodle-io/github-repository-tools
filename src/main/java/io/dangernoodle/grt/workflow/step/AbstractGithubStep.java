package io.dangernoodle.grt.workflow.step;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.dangernoodle.grt.Repository;
import io.dangernoodle.grt.Workflow;
import io.dangernoodle.grt.util.GithubClient;


/**
 * Abstract 'help' class that ensures a <code>GithubClient</code> and <code>Logger</code> are available.
 * 
 * @since 0.9.0
 */
public abstract class AbstractGithubStep implements Workflow.Step<Repository>
{
    protected final GithubClient client;

    protected final Logger logger;

    public AbstractGithubStep(GithubClient client)
    {
        this.client = client;
        this.logger = LoggerFactory.getLogger(getClass());
    }
}
