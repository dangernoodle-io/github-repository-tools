package io.dangernoodle.grt.workflow.step;

import static io.dangernoodle.grt.Constants.SHA1;
import static io.dangernoodle.grt.Constants.TAG;

import java.io.IOException;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHTag;

import io.dangernoodle.grt.Repository;
import io.dangernoodle.grt.Workflow.Context;
import io.dangernoodle.grt.Workflow.Status;
import io.dangernoodle.grt.util.GithubClient;


public abstract class FindCommitBy extends AbstractGithubStep
{
    public FindCommitBy(GithubClient client)
    {
        super(client);
    }

    @Override
    public Status execute(Repository repository, Context context) throws Exception
    {
        String key = getContextKey();

        if (context.contains(GHCommit.class))
        {
            logger.debug("existing commit found in context, skipping");
        }
        else if (context.contains(key))
        {
            String value = context.get(key).toString();
            GHCommit ghCommit = findCommit(context.getGHRepository(), value);

            logger.info("found commit object using [{} ({})]", value, key);
            context.add(ghCommit);
        }
        else
        {
            logger.warn("context did not contain value for [{}]", key);
        }

        return Status.CONTINUE;
    }

    protected abstract GHCommit findCommit(GHRepository ghRepo, String value) throws IOException;

    protected abstract String getContextKey();

    public static class Sha1 extends FindCommitBy
    {
        public Sha1(GithubClient client)
        {
            super(client);
        }

        @Override
        protected GHCommit findCommit(GHRepository ghRepo, String sha1) throws IOException
        {
            return ghRepo.getCommit(sha1);
        }

        @Override
        protected String getContextKey()
        {
            return SHA1;
        }
    }

    public static class Tag extends FindCommitBy
    {
        public Tag(GithubClient client)
        {
            super(client);
        }

        @Override
        protected GHCommit findCommit(GHRepository ghRepo, String tag) throws IOException
        {
            return getTagStream(ghRepo).filter(t -> tag.equals(t.getName()))
                                       .findFirst()
                                       .map(GHTag::getCommit)
                                       .orElse(null);
        }

        @Override
        protected String getContextKey()
        {
            return TAG;
        }

        Stream<GHTag> getTagStream(GHRepository ghRepo) throws IOException
        {
            return StreamSupport.stream(ghRepo.listTags().spliterator(), false);
        }
    }
}
