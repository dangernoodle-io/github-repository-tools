package io.dangernoodle.grt.steps;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.kohsuke.github.GHLabel;
import org.kohsuke.github.GHRepository;

import io.dangernoodle.grt.GithubClient;
import io.dangernoodle.grt.Repository;
import io.dangernoodle.grt.Repository.Settings.Color;
import io.dangernoodle.grt.Workflow.Context;
import io.dangernoodle.grt.Workflow.Status;
import io.dangernoodle.grt.internal.RepositoryWorkflow;


public class CreateRepositoryLabels extends RepositoryWorkflow.Step
{
    public CreateRepositoryLabels(GithubClient client)
    {
        super(client);
    }

    @Override
    public Status execute(Repository repository, Context context) throws IOException
    {
        GHRepository ghRepo = context.getGHRepository();

        Map<String, Color> labels = repository.getSettings().getLabels();
        Set<GHLabel> existing = ghRepo.listLabels().toSet();

        for (String name : labels.keySet())
        {
            GHLabel label = findLabel(name, existing);
            String color = labels.get(name).toString();

            if (label == null)
            {
                ghRepo.createLabel(name, color);
                logger.debug("created label [{} / {}]", name, color);
            }
            else if (!label.getColor().equals(color))
            {
                logger.warn("existing label [{} / {}] does not match [{} / {}]", name, color, name, label.getColor());
            }
            else
            {
                logger.debug("label [{} / {}] already exists", name, color);
            }
        }

        return Status.CONTINUE;
    }

    private GHLabel findLabel(String name, Set<GHLabel> existing)
    {
        return existing.stream()
                       .filter(label -> label.getName().equals(name))
                       .findFirst()
                       .orElse(null);
    }
}
