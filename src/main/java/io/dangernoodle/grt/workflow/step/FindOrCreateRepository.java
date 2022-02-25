package io.dangernoodle.grt.workflow.step;

import java.io.IOException;

import org.kohsuke.github.GHRepository;

import io.dangernoodle.grt.GithubClient;
import io.dangernoodle.grt.Repository;
import io.dangernoodle.grt.Workflow.Context;
import io.dangernoodle.grt.Workflow.Status;


public class FindOrCreateRepository extends AbstractGithubStep
{
    private final boolean create;

    public FindOrCreateRepository(GithubClient client, boolean create)
    {
        super(client);
        this.create = create;
    }

    public FindOrCreateRepository(GithubClient client)
    {
        this(client, true);
    }

    @Override
    public Status execute(Repository repository, Context context) throws IOException, IllegalStateException
    {
        String name = repository.getName();
        String organization = repository.getOrganization();

        boolean created = false;
        Delegate delegate = createDelegate(organization);

        GHRepository ghRepo = delegate.get(name, organization);

        if (ghRepo == null)
        {
            if (!create)
            {
                logger.error("repository [{}] does not exist, aborting...", repository.getFullName());
                throw new IllegalStateException();
            }

            created = true;
            ghRepo = delegate.create(repository);
        }

        context.add(ghRepo);
        logger.info("repository [{}] {}!", repository.getFullName(), created ? "created" : "found");

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
