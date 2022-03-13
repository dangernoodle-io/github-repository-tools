package io.dangernoodle.grt.workflow.step;

import static io.dangernoodle.grt.Constants.CLEAR_WEBHOOKS;

import java.io.IOException;
import java.util.List;

import org.kohsuke.github.GHHook;
import org.kohsuke.github.GHRepository;

import io.dangernoodle.grt.Repository;
import io.dangernoodle.grt.Workflow.Context;
import io.dangernoodle.grt.Workflow.Status;
import io.dangernoodle.grt.client.GithubClient;


public class ClearWebhooks extends AbstractGithubStep
{
    public ClearWebhooks(GithubClient client)
    {
        super(client);
    }

    @Override
    public Status execute(Repository repository, Context context) throws Exception
    {
        GHRepository ghRepo = context.getGHRepository();
        boolean clearWebhooks = context.get(CLEAR_WEBHOOKS, false);

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
