package io.dangernoodle.grt.steps;

import java.io.IOException;

import org.kohsuke.github.GHRepository;

import io.dangernoodle.grt.GithubClient;
import io.dangernoodle.grt.Repository;
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

        boolean created = false;
        Delegate delegate = createDelegate(organization);

        GHRepository ghRepo = delegate.get(name, organization);

        if (ghRepo == null)
        {
            created = true;
            ghRepo = delegate.create(repository);
        }

        if (!created)
        {
            logger.debug("updating repository settings...");
            updateRepository(ghRepo, repository);
        }

        context.add(ghRepo);
        context.setOrg(delegate.isOrg());

        logger.info("repository [{} / {}] {}", organization, name, created ? "created!" : "already exists!");
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

            @Override
            public boolean isOrg()
            {
                return false;
            }
        };
    }

    private void setOrUpdateDescription(GHRepository ghRepo, String description) throws IOException
    {
        if (description != null)
        {
            String ghDesc = ghRepo.getDescription();
            if (ghDesc == null)
            {
                logger.info("setting repository description to [{}]", description);
                ghRepo.setDescription(description);
            }
            else if (!ghDesc.equals(description))
            {
                logger.warn("description already set to [{}]", description);
            }
        }
    }

    private void setOrUpdateHomepage(GHRepository ghRepo, String homepage) throws IOException
    {
        if (homepage != null)
        {
            String ghHome = ghRepo.getHomepage();
            if (ghHome == null)
            {
                logger.info("setting repository homepage to [{}]", homepage);
                ghRepo.setHomepage(homepage);
            }
            else if (!ghHome.equals(homepage))
            {
                logger.warn("homepage already set to [{}]", ghHome);
            }
        }
    }

    private void updateRepository(GHRepository ghRepo, Repository repository) throws IOException
    {
        setOrUpdateDescription(ghRepo, repository.getDescription());
        setOrUpdateHomepage(ghRepo, repository.getHomepage());
    }

    private interface Delegate
    {
        GHRepository create(Repository repository) throws IOException;

        GHRepository get(String name, String organization) throws IOException;

        boolean isOrg();
    }
}
