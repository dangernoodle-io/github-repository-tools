package io.dangernoodle.grt.internal;

import static io.dangernoodle.grt.Constants.REPOSITORY;
import static io.dangernoodle.grt.Constants.UPDATE_REF;

import java.util.List;

import io.dangernoodle.grt.Repository;
import io.dangernoodle.grt.StatusCheck;
import io.dangernoodle.grt.Workflow;
import io.dangernoodle.grt.client.GithubClient;
import io.dangernoodle.grt.workflow.StepWorkflow;
import io.dangernoodle.grt.workflow.step.AddTeamsAndCollaborators;
import io.dangernoodle.grt.workflow.step.ClearWebhooks;
import io.dangernoodle.grt.workflow.step.CreateOrUpdateReference;
import io.dangernoodle.grt.workflow.step.CreateRepositoryBranches;
import io.dangernoodle.grt.workflow.step.CreateRepositoryLabels;
import io.dangernoodle.grt.workflow.step.EnableBranchProtections;
import io.dangernoodle.grt.workflow.step.FindCommitBy;
import io.dangernoodle.grt.workflow.step.FindOrCreateRepository;
import io.dangernoodle.grt.workflow.step.SetRepositoryOptions;


public final class DefaultWorkflows
{
    public static Workflow<Repository> repositoryWorkflow(GithubClient client, StatusCheck statusCheck)
    {
        return createStepWorkflow(REPOSITORY,
                new FindOrCreateRepository(client),
                new SetRepositoryOptions(client),
                new CreateRepositoryLabels(client),
                new AddTeamsAndCollaborators(client),
                new CreateRepositoryBranches(client),
                new EnableBranchProtections(client, statusCheck),
                // optional step enabled by a command line argument
                new ClearWebhooks(client));
    }
    
    public static Workflow<Repository> updateRefWorkflow(GithubClient client)
    {
        return createStepWorkflow(UPDATE_REF,
                new FindOrCreateRepository(client, false),
                new FindCommitBy.Tag(client),
                new FindCommitBy.Sha1(client),
                new CreateOrUpdateReference(client));
    }

    @SafeVarargs
    private static <T> StepWorkflow<T> createStepWorkflow(String name, Workflow.Step<T>... steps)
    {
        return new StepWorkflow<>(name, List.of(steps));
    }
}
