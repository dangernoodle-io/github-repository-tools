package io.dangernoodle.grt.steps;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.kohsuke.github.GHBranch;
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

        String ghDefault = ghRepo.getDefaultBranch();
        String defaultBranch = branches.getDefault();

        boolean isDefault = defaultBranch.equals(ghDefault);
        String commit = ghRepo.getBranch(ghDefault).getSHA1();

        if (commit == null)
        {
            logger.warn("no commit found for current default branch [{}], skipping branch creation", ghDefault);
        }
        else
        {
            createBranches(ghRepo, commit, getBranchesToCreate(branches, defaultBranch, ghRepo));
        }

        if (!isDefault)
        {
            logger.info("setting default branch to [{}]", defaultBranch);
            ghRepo.setDefaultBranch(defaultBranch);
        }
    }

    private void createBranches(GHRepository ghRepo, String commit, List<String> toCreate) throws IOException
    {
        for (String branch : toCreate)
        {
            String ref = "refs/heads/" + branch;
            logger.info("creating branch ref [{}] using commit [{}]", ref, commit);

            ghRepo.createRef(ref, commit);
        }
    }

    private List<String> getBranchesToCreate(Branches branches, String defaultBranch, GHRepository ghRepo)
        throws IOException
    {
        Map<String, GHBranch> ghBranches = ghRepo.getBranches();

        List<String> toCreate = branches.getOther()
                                        .stream()
                                        .filter(name -> !ghBranches.containsKey(name))
                                        .collect(Collectors.toList());

        if (!ghBranches.containsKey(defaultBranch))
        {
            toCreate.add(defaultBranch);
        }

        return toCreate;
    }
}
