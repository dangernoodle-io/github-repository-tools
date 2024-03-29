package io.dangernoodle.grt.workflow.step;

import java.io.IOException;

import org.kohsuke.github.GHRepository;

import io.dangernoodle.grt.Repository;
import io.dangernoodle.grt.Workflow.Context;
import io.dangernoodle.grt.Workflow.Status;
import io.dangernoodle.grt.client.GithubClient;


public class SetRepositoryOptions extends AbstractGithubStep
{
    public SetRepositoryOptions(GithubClient client)
    {
        super(client);
    }

    @Override
    public Status execute(Repository repository, Context context) throws IOException
    {
        boolean archived = repository.isArchived();
        GHRepository ghRepo = context.getGHRepository();

        if (archived)
        {
            return archive(ghRepo);
        }

        if (!archived && ghRepo.isArchived())
        {
            logger.error("unarchiving of projects must first be done via github, no further github actions will be taken!");
            return Status.SKIP;
        }

        logger.info("configuring repository settings and options");
        // store the updated object back into the context
        context.add(updateRepositoryOptions(repository, ghRepo));

        return Status.CONTINUE;
    }

    private Status archive(GHRepository ghRepo) throws IOException
    {
        if (!ghRepo.isArchived())
        {
            logger.debug("sending archive request to github...");
            ghRepo.archive();
        }

        logger.warn("repository is archived, no further github actions will be taken!");
        return Status.SKIP;
    }

    private <T> boolean requiresUpdate(T obj1, T obj2)
    {
        return obj1 != null && !obj1.equals(obj2);
    }

    private GHRepository updateRepositoryOptions(Repository repository, GHRepository ghRepo)
        throws IOException
    {
        boolean update = false;

        GHRepository.Updater updater = ghRepo.update();
        Repository.Settings settings = repository.getSettings();

        if (requiresUpdate(repository.getDescription(), ghRepo.getDescription()))
        {
            update = true;
            updater.description(repository.getDescription());
        }

        if (requiresUpdate(repository.getHomepage(), ghRepo.getHomepage()))
        {
            update = true;
            updater.homepage(repository.getHomepage());
        }

        if (requiresUpdate(settings.deleteBranchOnMerge(), ghRepo.isDeleteBranchOnMerge()))
        {
            update = true;
            updater.deleteBranchOnMerge(settings.deleteBranchOnMerge());
        }

        if (requiresUpdate(settings.enableMergeCommits(), ghRepo.isAllowMergeCommit()))
        {
            update = true;
            updater.allowMergeCommit(settings.enableMergeCommits());
        }

        if (requiresUpdate(settings.enableRebaseMerge(), ghRepo.isAllowRebaseMerge()))
        {
            update = true;
            updater.allowRebaseMerge(settings.enableRebaseMerge());
        }

        if (requiresUpdate(settings.enableSquashMerge(), ghRepo.isAllowSquashMerge()))
        {
            update = true;
            updater.allowSquashMerge(settings.enableSquashMerge());
        }

        if (requiresUpdate(settings.enableIssues(), ghRepo.hasIssues()))
        {
            update = true;
            updater.issues(settings.enableIssues());
        }

        if (requiresUpdate(settings.enableWiki(), ghRepo.hasWiki()))
        {
            update = true;
            updater.wiki(settings.enableWiki());
        }

        if (requiresUpdate(settings.isPrivate(), ghRepo.isPrivate()))
        {
            update = true;
            updater.private_(settings.isPrivate());
        }

        return update ? updater.done() : ghRepo;
    }
}
