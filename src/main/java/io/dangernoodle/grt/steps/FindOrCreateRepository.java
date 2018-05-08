package io.dangernoodle.grt.steps;

import java.io.IOException;

import org.kohsuke.github.GHRepository;

import io.dangernoodle.grt.GithubClient;
import io.dangernoodle.grt.Repository;
import io.dangernoodle.grt.Repository.Settings;
import io.dangernoodle.grt.Workflow.Context;
import io.dangernoodle.grt.internal.GithubWorkflow;


public class FindOrCreateRepository extends GithubWorkflow.Step
{
    public FindOrCreateRepository(GithubClient client)
    {
        super(client);
    }

    @Override
    public void execute(Repository repository, Context context) throws IOException
    {
        String name = repository.getName();
        String organization = repository.getOrganization();

        Delegate delegate = createDelegate(organization);

        boolean created = false;
        GHRepository ghRepo = delegate.get(name);

        if (ghRepo == null)
        {
            created = true;
            ghRepo = delegate.create(name, repository.getSettings());
        }

        context.add(ghRepo);
        context.setOrg(delegate.isOrg());

        logger.info("repository [{} / {}] {}", organization, name, toText(created));
    }

    private Delegate createDelegate(String organization) throws IOException
    {
        return organization.equals(client.getCurrentLogin()) ? createUserDelegate() : createOrgDelegate(organization);
    }

    private Delegate createOrgDelegate(String organization)
    {
        return new Delegate()
        {
            @Override
            public GHRepository create(String name, Settings settings) throws IOException
            {
                logger.debug("creating [{}] as organization repository", name);
                return client.createRepository(name, organization, settings);
            }

            @Override
            public GHRepository get(String name) throws IOException
            {
                return client.getRepository(organization, name);
            }

            @Override
            public boolean isOrg()
            {
                return true;
            }
        };
    }

    private Delegate createUserDelegate()
    {
        return new Delegate()
        {
            @Override
            public GHRepository create(String name, Settings settings) throws IOException
            {
                logger.debug("creating [{}] as user repository", name);
                return client.createRepository(name, settings);
            }

            @Override
            public GHRepository get(String name) throws IOException
            {
                return client.getRepository(name);
            }

            @Override
            public boolean isOrg()
            {
                return false;
            }
        };
    }

    private String toText(boolean created)
    {
        return created ? "has been created" : "exists, creation skipped";
    }

    private interface Delegate
    {
        GHRepository create(String name, Settings settings) throws IOException;

        GHRepository get(String name) throws IOException;

        boolean isOrg();
    }
}
