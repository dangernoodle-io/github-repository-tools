package io.dangernoodle.grt.cli.options;

import static io.dangernoodle.grt.Constants.REF_OPT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import picocli.CommandLine.Mixin;


public class ReferenceOptionTest extends AbstractOptionTest
{
    private static final String REF_NAME = "refs/heads/foo";

    @Mixin
    private ReferenceOption ref;

    @Test
    public void testInvalidRef()
    {
        givenAnInvalidRef();
        thenParseHasErrors();
    }

    @Test
    public void testReference()
    {
        givenAReference();
        whenParseArguments();
        thenReferenceInMap();
    }

    @Test
    public void testRequired()
    {
        thenParseHasErrors();
    }

    private void givenAnInvalidRef()
    {
        args.add(createOption(REF_OPT, "invalid"));
    }

    private void givenAReference()
    {
        args.add(createOption(REF_OPT, REF_NAME));
    }

    private void thenReferenceInMap()
    {
        assertEquals(1, ref.toArgMap().size());
        assertTrue(ref.toArgMap().containsValue(REF_NAME));
    }
}
