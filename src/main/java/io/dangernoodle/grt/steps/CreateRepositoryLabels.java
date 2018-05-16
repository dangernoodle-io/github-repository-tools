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
import io.dangernoodle.grt.internal.GithubWorkflow;


public class CreateRepositoryLabels extends GithubWorkflow.Step
{
    public CreateRepositoryLabels(GithubClient client)
    {
        super(client);
    }

    @Override
    public void execute(Repository repository, Context context) throws IOException
    {
        GHRepository ghRepo = context.get(GHRepository.class);

        Map<String, Color> labels = repository.getSettings().getLabels();
        Set<GHLabel> existing = ghRepo.listLabels().asSet();

        for (String name : labels.keySet())
        {
            GHLabel label = findLabel(name, existing);
            String color = labels.get(name).toString();

            if (label == null)
            {
                ghRepo.createLabel(name, color);
                logger.info("created label [{} / {}]", name, color);
            }
            else if (!label.getColor().equals(color))
            {
                logger.warn("existing label [{} / {}] does not match [{} / {}]", name, color, name, label.getColor());
            }
            else
            {
                logger.info("label [{} / {}] already exists", name, color);
            }
        }
    }

    private GHLabel findLabel(String name, Set<GHLabel> existing)
    {
        return existing.stream()
                       .filter(label -> label.getName().equals(name))
                       .findFirst()
                       .orElse(null);
    }
}
