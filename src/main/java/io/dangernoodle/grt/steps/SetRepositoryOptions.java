package io.dangernoodle.grt.steps;

import java.io.IOException;

import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHUpdateRepositoryBuilder;

import io.dangernoodle.grt.GithubClient;
import io.dangernoodle.grt.Repository;
import io.dangernoodle.grt.Workflow.Context;
import io.dangernoodle.grt.Workflow.Status;
import io.dangernoodle.grt.internal.GithubWorkflow;


public class SetRepositoryOptions extends GithubWorkflow.Step
{
    public SetRepositoryOptions(GithubClient client)
    {
        super(client);
    }

    @Override
    public Status execute(Repository repository, Context context) throws IOException
    {
        GHRepository ghRepo = context.getGHRepository();
        boolean archived = repository.getSettings().isArchived();

        if (archived)
        {
            return archive(ghRepo);
        }
        
        if (!archived && ghRepo.isArchived())
        {
            logger.warn("** unarchiving of projects must first be done via github, no further github actions will be taken!");
            return Status.SKIP;
        }
        
        logger.info("** configuring repository options...");
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

        logger.info("** repository is archived, no further github actions will be taken!");
        return Status.SKIP;
    }

    private GHRepository updateRepositoryOptions(Repository repository, GHRepository ghRepo) 
            throws IOException
    {
        boolean update = false;
        
        GHUpdateRepositoryBuilder builder = ghRepo.updateRepository();
        Repository.Settings settings = repository.getSettings();
        
        if (repository.getDescription() != ghRepo.getDescription()) {
            update = true;
            builder.description(repository.getDescription());
        }
        
        if (repository.getHomepage() != ghRepo.getHomepage()) {
            update = true;
            builder.homepage(repository.getHomepage());
        }
        
        if (settings.enableMergeCommits() != ghRepo.isAllowMergeCommit()) {
            update = true;
            builder.allowMergeCommit(settings.enableMergeCommits());
        }
    
        if (settings.enableRebaseMerge() != ghRepo.isAllowRebaseMerge()) {
            update = true;
            builder.allowRebaseMerge(settings.enableRebaseMerge());
        }
        
        if (settings.enableSquashMerge() != ghRepo.isAllowSquashMerge()) {
            update = true;
            builder.allowSquashMerge(settings.enableSquashMerge());
        }
        
        if (settings.enableIssues() != ghRepo.hasIssues()) {
            update = true;
            builder.issues(settings.enableIssues());
        }

        if (settings.enableWiki() != ghRepo.hasWiki()) {
            update = true;
            builder.wiki(settings.enableWiki());
        }
        
        if (settings.isPrivate() != ghRepo.isPrivate()) {
            update = true;
            builder.private_(settings.isPrivate());
        }

        return update ? builder.update() : ghRepo;
    }
}
