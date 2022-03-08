package io.dangernoodle.grt.statuscheck;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.dangernoodle.grt.Repository;
import io.dangernoodle.grt.StatusCheck;


public class CommandStatusCheckTest
{
    private static final String MAIN = "main";

    private CommandStatusCheck check;

    @Mock
    private Repository mockRepository;

    @Mock
    private StatusCheck mockStatusCheck;

    private Collection<String> required;

    @BeforeEach
    public void beforeEach()
    {
        MockitoAnnotations.initMocks(this);
        when(mockStatusCheck.getRequiredChecks(MAIN, mockRepository)).thenReturn(List.of("check"));

        check = new CommandStatusCheck("command", mockStatusCheck);
    }

    @Test
    public void testCommandHasChecks()
    {
        givenCommandHasChecks();
        whenGetRequredChecks();
        thenChecksAreReturned();
    }

    @Test
    public void testCommandHasNoChecks()
    {
        givenCommandHasNoChecks();
        whenGetRequredChecks();
        thenChecksAreEmpty();
    }

    @Test
    public void testGetCommands()
    {
        assertThrows(UnsupportedOperationException.class, check::getCommands);
    }

    private void givenCommandHasChecks()
    {
        when(mockStatusCheck.getCommands()).thenReturn(List.of("command"));
    }

    private void givenCommandHasNoChecks()
    {
        when(mockStatusCheck.getCommands()).thenReturn(List.of("other"));
    }

    private void thenChecksAreEmpty()
    {
        assertTrue(required.isEmpty());
    }

    private void thenChecksAreReturned()
    {
        assertFalse(required.isEmpty());
    }

    private void whenGetRequredChecks()
    {
        required = check.getRequiredChecks(MAIN, mockRepository);
    }
}
