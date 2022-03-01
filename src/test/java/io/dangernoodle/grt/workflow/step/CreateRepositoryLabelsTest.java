package io.dangernoodle.grt.workflow.step;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kohsuke.github.GHLabel;
import org.kohsuke.github.PagedIterable;
import org.mockito.Mock;

import io.dangernoodle.grt.Repository.Settings.Color;


public class CreateRepositoryLabelsTest extends AbstractGithubWorkflowStepTest
{
    @Mock
    private GHLabel mockGHLabel;

    @Mock
    private PagedIterable<GHLabel> mockPagedIterable;

    @Override
    @BeforeEach
    public void beforeEach() throws Exception
    {
        super.beforeEach();

        when(mockGHLabel.getName()).thenReturn("name");
        when(mockGHRepository.listLabels()).thenReturn(mockPagedIterable);
    }

    @Test
    public void testCreateNewLabel() throws Exception
    {
        givenALabelToCreate();
        whenExecuteStep();
        thenLabelWasCreated();
        thenStatusIsContinue();
    }

    @Test
    public void testLabelAlreadyExists() throws Exception
    {
        givenALabelToCreate();
        givenALabelThatAlreadyExists();
        whenExecuteStep();
        thenLabelWasNotCreated();
        thenStatusIsContinue();
    }

    @Test
    public void testLabelColorsDontMatch() throws Exception
    {
        givenALabelToCreate();
        givenAnExistingNonMatchingColors();
        whenExecuteStep();
        // not the best but works for now
        thenLabelWasNotCreated();
        thenStatusIsContinue();
    }

    @Override
    protected AbstractGithubStep createStep()
    {
        return new CreateRepositoryLabels(mockClient);
    }

    private void givenALabelThatAlreadyExists() throws IOException
    {
        when(mockGHLabel.getColor()).thenReturn("000000");
        when(mockPagedIterable.toSet()).thenReturn(new HashSet<>(Arrays.asList(mockGHLabel)));
    }

    private void givenALabelToCreate()
    {
        repoBuilder.addLabel("name", Color.from("000000"));
    }

    private void givenAnExistingNonMatchingColors() throws IOException
    {
        when(mockGHLabel.getColor()).thenReturn("000001");
        when(mockPagedIterable.toSet()).thenReturn(new HashSet<>(Arrays.asList(mockGHLabel)));
    }

    private void thenLabelWasCreated() throws IOException
    {
        verify(mockGHRepository).createLabel("name", "000000");
    }

    private void thenLabelWasNotCreated() throws IOException
    {
        verify(mockGHRepository, times(0)).createLabel("name", "000000");
    }
}
