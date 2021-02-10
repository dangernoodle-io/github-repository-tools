package io.dangernoodle.grt.steps;

import java.io.IOException;
import java.util.List;

import org.kohsuke.github.GHHook;
import org.kohsuke.github.GHRepository;

import io.dangernoodle.grt.GithubClient;
import io.dangernoodle.grt.Repository;
import io.dangernoodle.grt.Workflow.Context;
import io.dangernoodle.grt.Workflow.Status;
import io.dangernoodle.grt.internal.GithubWorkflow;


public class ClearWebhooks extends GithubWorkflow.Step
{
    static final String CLEAR_WEBHOOKS = "clearWebhooks";

    public ClearWebhooks(GithubClient client)
    {
        super(client);
    }

    @Override
    public Status execute(Repository repository, Context context) throws Exception
    {
        GHRepository ghRepo = context.getGHRepository();
        boolean clearWebhooks = context.getArg(CLEAR_WEBHOOKS, false);

        if (clearWebhooks)
        {
            List<GHHook> hooks = ghRepo.getHooks();
            if (!hooks.isEmpty())
            {
                logger.warn("clearing any existing webhooks");
                hooks.forEach(this::delete);
            }
        }

        return Status.CONTINUE;
    }

    private void delete(GHHook hook)
    {
        try
        {
            hook.delete();
        }
        catch (IOException e)
        {
            logger.error("failed to remove webhook [{} - {}]", hook.getName(), hook.getId(), e.getMessage());
        }
    }
}
