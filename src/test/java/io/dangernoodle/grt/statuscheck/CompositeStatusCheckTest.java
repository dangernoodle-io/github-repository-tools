package io.dangernoodle.grt.statuscheck;

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
import io.dangernoodle.grt.StatusCheck;


public class CompositeStatusCheckTest
{
    private static final String MASTER = "master";

    private Collection<String> actual;

    private Collection<String> expected;

    @Mock
    private StatusCheck mockProvider1;

    @Mock
    private StatusCheck mockProvider2;

    @Mock
    private StatusCheck mockProvider3;

    private Repository mockRepository;

    private CompositeStatusCheck provider;

    @BeforeEach
    public void beforeEach()
    {
        MockitoAnnotations.initMocks(this);

        expected = new ArrayList<>();
        provider = new CompositeStatusCheck(mockProvider1, mockProvider2, mockProvider3);
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

    private Collection<String> mockRequiredChecks(StatusCheck mockProvider, Collection<String> toReturn)
    {
        when(mockProvider.getRequiredChecks(MASTER, mockRepository)).thenReturn(toReturn);

        return toReturn;
    }

    private void thenStatusChecksAreCorrect()
    {
        assertThat(actual.size(), equalTo(2));

        verify(mockProvider1).getRequiredChecks(MASTER, mockRepository);
        verify(mockProvider2).getRequiredChecks(MASTER, mockRepository);
        verify(mockProvider3).getRequiredChecks(MASTER, mockRepository);
    }

    private void whenGetStatusChecks()
    {
        actual = provider.getRequiredChecks(MASTER, mockRepository);
    }
}
