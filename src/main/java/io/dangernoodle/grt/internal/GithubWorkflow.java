package io.dangernoodle.grt.internal;

import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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


public class GithubWorkflow extends Workflow.Basic
{
    private final GithubClient client;

    private final StatusCheckProvider factory;

    public GithubWorkflow(GithubClient client, StatusCheckProvider factory)
    {
        this.client = client;
        this.factory = factory;
    }

    @Override
    public String getName()
    {
        return "github";
    }

    @Override
    protected Collection<Workflow.Step> createSteps()
    {
        Collection<Workflow.Step> steps = new ArrayList<>();

        steps.add(new FindOrCreateRepository(client));
        steps.add(new SetRepositoryOptions(client));
        steps.add(new CreateRepositoryLabels(client));
        steps.add(new AddTeamsAndCollaborators(client));
        steps.add(new CreateRepositoryBranches(client));
        steps.add(new EnableBranchProtections(client, factory));

        // optional step enabled by a command line argument
        steps.add(new ClearWebhooks(client));
        
        return steps;
    }

    public static abstract class Step implements Workflow.Step
    {
        protected final GithubClient client;

        protected final Logger logger;

        public Step(GithubClient client)
        {
            this.client = client;
            this.logger = LoggerFactory.getLogger(getClass());
        }
    }
}
