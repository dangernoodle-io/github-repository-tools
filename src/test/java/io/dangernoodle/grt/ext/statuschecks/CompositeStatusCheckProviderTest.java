package io.dangernoodle.grt.ext.statuschecks;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.dangernoodle.grt.Repository;


public class CompositeStatusCheckProviderTest
{
    private static final String MASTER = "master";

    private Collection<String> actual;

    private Collection<String> expected;

    @Mock
    private StatusCheckProvider mockProvider1;

    @Mock
    private StatusCheckProvider mockProvider2;

    @Mock
    private StatusCheckProvider mockProvider3;

    private Repository mockRepository;

    private CompositeStatusCheckProvider provider;

    @BeforeEach
    public void beforeEach()
    {
        MockitoAnnotations.initMocks(this);

        expected = new ArrayList<>();
        provider = new CompositeStatusCheckProvider(mockProvider1, mockProvider2, mockProvider3);
    }

    @Test
    public void testGetStatusChecks()
    {
        givenProvider1Checks();
        givenProvider2Checks();
        givenProvider3Checks();
        whenGetStatusChecks();
        thenStatusChecksAreCorrect();
    }

    private void givenProvider1Checks()
    {
        expected.addAll(mockRequiredChecks(mockProvider1, Arrays.asList("mockProvider1")));
    }

    private void givenProvider2Checks()
    {
        expected.addAll(mockRequiredChecks(mockProvider2, Collections.emptyList()));
    }

    private void givenProvider3Checks()
    {
        expected.addAll(mockRequiredChecks(mockProvider3, Arrays.asList("mockProvider1", "mockProvider3")));
    }

    private Collection<String> mockRequiredChecks(StatusCheckProvider mockProvider, Collection<String> toReturn)
    {
        when(mockProvider.getRequiredStatusChecks(MASTER, mockRepository)).thenReturn(toReturn);

        return toReturn;
    }

    private void thenStatusChecksAreCorrect()
    {
        assertThat(actual.size(), equalTo(2));

        verify(mockProvider1).getRequiredStatusChecks(MASTER, mockRepository);
        verify(mockProvider2).getRequiredStatusChecks(MASTER, mockRepository);
        verify(mockProvider3).getRequiredStatusChecks(MASTER, mockRepository);
    }

    private void whenGetStatusChecks()
    {
        actual = provider.getRequiredStatusChecks(MASTER, mockRepository);
    }
}
