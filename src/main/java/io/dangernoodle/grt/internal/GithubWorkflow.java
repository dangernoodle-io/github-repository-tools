package io.dangernoodle.grt.internal;

import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.dangernoodle.grt.GithubClient;
import io.dangernoodle.grt.Repository;
import io.dangernoodle.grt.Workflow;
import io.dangernoodle.grt.ext.statuschecks.StatusCheckProvider;
import io.dangernoodle.grt.steps.AddTeamsAndCollaborators;
import io.dangernoodle.grt.steps.CreateRepositoryBranches;
import io.dangernoodle.grt.steps.CreateRepositoryLabels;
import io.dangernoodle.grt.steps.EnableBranchProtections;
import io.dangernoodle.grt.steps.FindOrCreateRepository;


public class GithubWorkflow implements Workflow
{
    private static final Logger logger = LoggerFactory.getLogger(GithubWorkflow.class);

    private final GithubClient client;

    private final StatusCheckProvider factory;

    public GithubWorkflow(GithubClient client, StatusCheckProvider factory)
    {
        this.client = client;
        this.factory = factory;
    }

    @Override
    public void execute(Repository repository, Context context) throws Exception
    {
        Collection<GithubWorkflow.Step> steps = new ArrayList<>();

        steps.add(new FindOrCreateRepository(client));
        steps.add(new CreateRepositoryLabels(client));
        steps.add(new AddTeamsAndCollaborators(client));
        steps.add(new CreateRepositoryBranches(client));
        steps.add(new EnableBranchProtections(client, factory));

        for (GithubWorkflow.Step step : steps)
        {
            logger.trace("executing step [{}]", step.getClass().getName());
            step.execute(repository, context);
        }
    }

    @Override
    public String getName()
    {
        return "github";
    }

    public static abstract class Step implements Workflow.Step
    {
        protected GithubClient client;

        protected final Logger logger;

        protected Step(GithubClient client)
        {
            this.client = client;
            this.logger = LoggerFactory.getLogger(getClass());
        }
    }
}
