package io.dangernoodle.grt.steps;

import static io.dangernoodle.grt.utils.GithubRepositoryToolsUtils.toSha1;

import java.io.IOException;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHTag;

import io.dangernoodle.grt.GithubClient;
import io.dangernoodle.grt.Repository;
import io.dangernoodle.grt.Workflow.Context;
import io.dangernoodle.grt.Workflow.Status;

/**
 * @since 0.8.0
 */
public class FindCommitByTag extends AbstractGithubStep
{
    public FindCommitByTag(GithubClient client)
    {
        super(client);
    }

    @Override
    public Status execute(Repository repository, Context context) throws Exception
    {
        String commitTag = context.getArg("commitTag");
        GHRepository ghRepo = context.getGHRepository();

        Stream<GHTag> stream = getTagStream(ghRepo);
        GHTag ghTag = stream.filter(t -> commitTag.equals(t.getName()))
                            .findFirst()
                            .orElseThrow(() -> new IllegalStateException("failed to find commit for tag [" + commitTag + "]"));

        GHCommit ghCommit = ghTag.getCommit();
        logger.info("found commit [{}] for tag [{}]", toSha1(ghCommit), commitTag);

        context.add(ghCommit);
        return Status.CONTINUE;
    }

    Stream<GHTag> getTagStream(GHRepository ghRepo) throws IOException
    {
        return StreamSupport.stream(ghRepo.listTags().spliterator(), false);
    }
}
