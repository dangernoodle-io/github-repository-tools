package io.dangernoodle.grt.cli.options;

import static io.dangernoodle.grt.Constants.SHA1;
import static io.dangernoodle.grt.Constants.SHA1_OPT;
import static io.dangernoodle.grt.Constants.TAG_OPT;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import picocli.CommandLine.ArgGroup;


public class Sha1OrTagOptionTest extends AbstractOptionTest
{
    private static final String HASH = "3ac68f502267";

    private static final String TAG_NAME = "tag";

    @ArgGroup(exclusive = true, multiplicity = "1")
    private Sha1OrTagOption sha1OrTag;

    @Test
    public void testInvalidSha1()
    {
        givenAnInvalidSha1();
        thenParseHasErrors();
    }
    
    @Test
    public void testRequired()
    {
        thenParseHasErrors();
    }

    @Test
    public void testSha1()
    {
        givenASha1();
        whenParseArguments();
        thenMapContainsSha1();
    }

    @Test
    public void testTag()
    {
        givenATag();
        whenParseArguments();
        thenMapContainsTag();
    }

    private void givenAnInvalidSha1()
    {
        args.add(createOption(SHA1_OPT, "invalid"));
    }

    private void givenASha1()
    {
        args.add(createOption(SHA1_OPT, HASH));
    }

    private void givenATag()
    {
        args.add(createOption(TAG_OPT, TAG_NAME));
    }

    private void thenMapContainsSha1()
    {
        assertEquals(1, sha1OrTag.toArgMap().size());
        assertEquals(HASH, sha1OrTag.toArgMap().get(SHA1));
    }

    private void thenMapContainsTag()
    {
        assertEquals(1, sha1OrTag.toArgMap().size());
        assertEquals(TAG_NAME, sha1OrTag.toArgMap().get(TAG_NAME));
    }
}
