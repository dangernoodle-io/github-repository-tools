package io.dangernoodle.grt.cli;

import static io.dangernoodle.grt.Constants.REF_OPT;
import static io.dangernoodle.grt.Constants.UPDATE_REF;

import com.google.inject.Injector;

import io.dangernoodle.grt.cli.exector.UpdateRefExecutor;
import io.dangernoodle.grt.util.CommandArguments.Sha1orTag;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;


@Command(name = UPDATE_REF)
public class UpdateRefCommand extends io.dangernoodle.grt.Command.DefinitionOnly
{
    @Option(names = REF_OPT, required = true)
    private String ref;

    @ArgGroup(exclusive = true, multiplicity = "1")
    private Sha1orTag sha1orTag;

    public UpdateRefCommand(Injector injector)
    {
        super(injector);
    }

    public String getRef()
    {
        return ref;
    }

    public String getSha1()
    {
        return sha1orTag.getSha1();
    }

    public String getTag()
    {
        return sha1orTag.getTag();
    }

    @Override
    protected Class<? extends io.dangernoodle.grt.Command.Executor> getExecutor()
    {
        return UpdateRefExecutor.class;
    }
}
