package io.dangernoodle.grt.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.dangernoodle.grt.GithubClient;
import io.dangernoodle.grt.Repository;
import io.dangernoodle.grt.Workflow;
import io.dangernoodle.grt.steps.AddTeamsAndCollaborators;
import io.dangernoodle.grt.steps.CreateRepositoryBranches;
import io.dangernoodle.grt.steps.CreateRepositoryLabels;
import io.dangernoodle.grt.steps.EnableBranchProtections;
import io.dangernoodle.grt.steps.FindOrCreateRepository;


@ApplicationScoped
public class GithubWorkflow implements Workflow
{
    private static final Logger logger = LoggerFactory.getLogger(GithubWorkflow.class);

    private final GithubClient client;

    @Inject
    public GithubWorkflow(GithubClient client)
    {
        this.client = client;
    }

    @Override
    public void execute(Repository repository, Context context) throws IOException
    {
        Collection<GithubWorkflow.Step> steps = new ArrayList<>();

        steps.add(new FindOrCreateRepository(client));
        steps.add(new CreateRepositoryLabels(client));
        steps.add(new AddTeamsAndCollaborators(client));
        steps.add(new CreateRepositoryBranches(client));
        steps.add(new EnableBranchProtections(client));

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
