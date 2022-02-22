package io.dangernoodle.grt.internal;

import java.util.List;

import io.dangernoodle.grt.GithubClient;
import io.dangernoodle.grt.Workflow;
import io.dangernoodle.grt.ext.statuschecks.StatusCheckProvider;
import io.dangernoodle.grt.steps.AddTeamsAndCollaborators;
import io.dangernoodle.grt.steps.ClearWebhooks;
import io.dangernoodle.grt.steps.CreateRepositoryBranches;
import io.dangernoodle.grt.steps.CreateRepositoryLabels;
import io.dangernoodle.grt.steps.EnableBranchProtections;
import io.dangernoodle.grt.steps.FindOrCreateRepository;
import io.dangernoodle.grt.steps.SetRepositoryOptions;
import io.dangernoodle.grt.workflows.StepWorkflow;


public final class GithubWorkflowsFactory
{
    public StepWorkflow repositoryWorkflow(GithubClient client, StatusCheckProvider factory)
    {
        return createStepWorkflow(
                new FindOrCreateRepository(client),
                new SetRepositoryOptions(client),
                new CreateRepositoryLabels(client),
                new AddTeamsAndCollaborators(client),
                new CreateRepositoryBranches(client),
                new EnableBranchProtections(client, factory),
                // optional step enabled by a command line argument
                new ClearWebhooks(client));
    }

    private StepWorkflow createStepWorkflow(Workflow.Step... steps)
    {
        return new StepWorkflow(List.of(steps));
    }
}
