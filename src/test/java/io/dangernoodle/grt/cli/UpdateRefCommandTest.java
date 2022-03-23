package io.dangernoodle.grt.cli;

import static io.dangernoodle.grt.Constants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class UpdateRefCommandTest extends AbstractCommandTest<UpdateRefCommand>
{
    private static final String HASH = "3ac68f502267";

    private static final String REF_NAME = "refs/heads/foo";
    
    private static final String TAG_NAME = "tag";
    
    @Test
    public void testRequired()
    {
        thenParseHasErrors();
    }
    
    @Test
    public void testSha1()
    {
        givenASha1();
        givenARef();
        givenADefinition();
        whenParseArguments();
        thenDefinitionMatches();
        thenAutoAddToWorkflowEnabled();
        thenSha1InArgs();
    }

    @Test
    public void testTag()
    {
        givenATag();
        givenARef();
        givenADefinition();
        whenParseArguments();
        thenDefinitionMatches();
        thenAutoAddToWorkflowEnabled();
        thenTagInArgs();
    }

    @Override
    protected UpdateRefCommand createCommand()
    {
        return new UpdateRefCommand(mockInjector);
    }
    
    private void givenARef()
    {
        args.add(createOption(REF_OPT, REF_NAME));
    }

    private void givenASha1()
    {
        args.add(createOption(SHA1_OPT, HASH));
    }

    private void givenATag()
    {
        args.add(createOption(TAG_OPT, TAG_NAME));
    }

    private void thenSha1InArgs()
    {
        assertTrue(command.toArgMap().containsKey(SHA1));
        assertEquals(HASH, command.toArgMap().get(SHA1));
    }

    private void thenTagInArgs()
    {
        assertTrue(command.toArgMap().containsKey(TAG));
        assertEquals(TAG_NAME, command.toArgMap().get(TAG));
    }
}
