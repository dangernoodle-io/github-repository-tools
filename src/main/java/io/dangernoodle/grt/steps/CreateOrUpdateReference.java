package io.dangernoodle.grt.steps;

import java.io.IOException;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHRef;
import org.kohsuke.github.GHRepository;

import io.dangernoodle.grt.GithubClient;
import io.dangernoodle.grt.Repository;
import io.dangernoodle.grt.Workflow.Context;
import io.dangernoodle.grt.Workflow.Status;


/**
 * @since 0.8.0
 */
public class CreateOrUpdateReference extends AbstractGithubStep
{
    public CreateOrUpdateReference(GithubClient client)
    {
        super(client);
    }

    @Override
    public Status execute(Repository repository, Context context) throws Exception
    {
        GHRepository ghRepo = context.getGHRepository();

        String ref = context.getArg("refName");
        String sha1 = context.get(GHCommit.class).getSHA1();

        Stream<GHRef> stream = getRefStream(ghRepo);
        GHRef ghRef = stream.filter(r -> r.getRef().equals(ref))
                            .findFirst()
                            .orElse(null);

        if (ghRef == null)
        {
            logger.info("creating reference [{}] using commit [{}]", ref, sha1.substring(0, 12));
            ghRepo.createRef(ref, sha1);
        }
        else
        {
            logger.info("updating reference [{}] to commit [{}]", ref, sha1.substring(0, 12));
            ghRef.updateTo(sha1, true);
        }

        return Status.CONTINUE;
    }

    Stream<GHRef> getRefStream(GHRepository ghRepo) throws IOException
    {
        return StreamSupport.stream(ghRepo.listRefs().spliterator(), false);
    }
}
