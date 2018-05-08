package io.dangernoodle.grt.steps;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

import org.kohsuke.github.GHRepository;

import io.dangernoodle.grt.GithubClient;
import io.dangernoodle.grt.Repository;
import io.dangernoodle.grt.Repository.Settings.Branches;
import io.dangernoodle.grt.Workflow.Context;
import io.dangernoodle.grt.internal.GithubWorkflow;


public class CreateRepositoryBranches extends GithubWorkflow.Step
{
    public CreateRepositoryBranches(GithubClient client)
    {
        super(client);
    }

    @Override
    public void execute(Repository repository, Context context) throws IOException
    {
        GHRepository ghRepo = context.get(GHRepository.class);
        Branches branches = repository.getSettings().getBranches();

        String defaultBranch = branches.getDefault();
        boolean isDefault = defaultBranch.equals(ghRepo.getDefaultBranch());

        Collection<String> toCreate = new HashSet<>(branches.getOther());

        if (!isDefault)
        {
            toCreate.add(defaultBranch);
        }

//        for (String name : toCreate)
//        {
//            ghRepo.listCommits()
//        }

        // System.out.println("default: " + ghRepo.getDefaultBranch());

    }
}
