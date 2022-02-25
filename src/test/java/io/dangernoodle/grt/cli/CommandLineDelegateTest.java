//package io.dangernoodle.grt.cli;
//
//import static org.hamcrest.MatcherAssert.assertThat;
//import static org.hamcrest.Matchers.equalTo;
//import static org.hamcrest.Matchers.instanceOf;
//import static org.hamcrest.Matchers.notNullValue;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//import java.util.Arrays;
//import java.util.Collections;
//
//import com.beust.jcommander.JCommander;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import io.dangernoodle.grt.Arguments;
//import io.dangernoodle.grt.Command;
//
//
//public class CommandLineDelegateTest
//{
//    private String[] args;
//
//    private Command command;
//
//    private Exception exception;
//
//    @Mock
//    private Arguments mockArguments;
//
//    @Mock
//    private Command mockCommand;
//
//    @Mock
//    private JCommander mockJCommander;
//
//    private CommandLineParser parser;
//
//    @BeforeEach
//    public void beforeEach()
//    {
//        MockitoAnnotations.initMocks(this);
//
//        when(mockJCommander.getObjects()).thenReturn(Arrays.asList(mockCommand));
//        when(mockJCommander.getCommands()).thenReturn(Collections.singletonMap("command", mockJCommander));
//
//        parser = new CommandLineParser(Arrays.asList(mockCommand), mockArguments)
//        {
//            @Override
//            JCommander createJCommander(Arguments arguments)
//            {
//                return mockJCommander;
//            }
//        };
//    }
//
//    @Test
//    public void testEmptyCommand()
//    {
//        givenAnEmptyCommand();
//        whenParseArgs();
//        thenUsageIsPrinted();
//        thenExceptionIsThrown();
//    }
//
//    @Test
//    public void testNullCommand()
//    {
//        whenParseArgs();
//        thenUsageIsPrinted();
//        thenExceptionIsThrown();
//    }
//
//    @Test
//    public void testUnknownCommand()
//    {
//        givenAnUnknownCommand();
//        whenParseArgs();
//        thenUsageIsPrinted();
//        thenExceptionIsThrown();
//    }
//
//    @Test
//    public void testValidCommand()
//    {
//        givenValidArguments();
//        whenParseArgs();
//        thenCommandIsFound();
//    }
//
//    private void givenAnEmptyCommand()
//    {
//        args = new String[] { "" };
//    }
//
//    private void givenAnUnknownCommand()
//    {
//        args = new String[] { "unknown" };
//    }
//
//    private void givenValidArguments()
//    {
//        args = new String[] { "command" };
//        when(mockJCommander.getParsedCommand()).thenReturn(args[0]);
//    }
//
//    private void thenCommandIsFound()
//    {
//        assertThat(command, equalTo(mockCommand));
//    }
//
//    private void thenExceptionIsThrown()
//    {
//        assertThat(exception, notNullValue());
//        assertThat(exception, instanceOf(IllegalArgumentException.class));
//    }
//
//    private void thenUsageIsPrinted()
//    {
//        verify(mockJCommander).usage();
//    }
//
//    private void whenParseArgs()
//    {
//        try
//        {
//            command = parser.parse(args);
//        }
//        catch (Exception e)
//        {
//            exception = e;
//        }
//    }
//}
