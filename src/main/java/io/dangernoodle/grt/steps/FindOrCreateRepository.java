package io.dangernoodle.grt.steps;

import java.io.IOException;

import org.kohsuke.github.GHRepository;

import io.dangernoodle.grt.GithubClient;
import io.dangernoodle.grt.Repository;
import io.dangernoodle.grt.Workflow.Context;
import io.dangernoodle.grt.Workflow.Status;
import io.dangernoodle.grt.internal.GithubWorkflow;


public class FindOrCreateRepository extends GithubWorkflow.Step
{
    public FindOrCreateRepository(GithubClient client)
    {
        super(client);
    }

    @Override
    public Status execute(Repository repository, Context context) throws IOException
    {
        String name = repository.getName();
        String organization = repository.getOrganization();

        boolean created = false;
        Delegate delegate = createDelegate(organization);

        GHRepository ghRepo = delegate.get(name, organization);

        if (ghRepo == null)
        {
            ghRepo = delegate.create(repository);
        }

        context.add(ghRepo);
        logger.info("repository [{} / {}] {}", organization, name, created ? "created" : "already exists");

        return Status.CONTINUE;
    }

    private Delegate createDelegate(String organization) throws IOException
    {
        return organization.equals(client.getCurrentLogin()) ? createUserDelegate() : createOrgDelegate();
    }

    private Delegate createOrgDelegate()
    {
        return new Delegate()
        {
            @Override
            public GHRepository create(Repository repository) throws IOException
            {
                logger.debug("creating [{}] as organization repository", repository.getName());
                return client.createOrgRepository(repository);
            }

            @Override
            public GHRepository get(String name, String organization) throws IOException
            {
                return client.getRepository(organization, name);
            }
        };
    }

    private Delegate createUserDelegate()
    {
        return new Delegate()
        {
            @Override
            public GHRepository create(Repository repository) throws IOException
            {
                logger.debug("creating [{}] as user repository", repository.getName());
                return client.createUserRepository(repository);
            }

            @Override
            public GHRepository get(String name, String organization) throws IOException
            {
                return client.getRepository(name);
            }
        };
    }

    private interface Delegate
    {
        GHRepository create(Repository repository) throws IOException;

        GHRepository get(String name, String organization) throws IOException;
    }
}
