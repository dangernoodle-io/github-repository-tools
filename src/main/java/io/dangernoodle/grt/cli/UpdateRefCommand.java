package io.dangernoodle.grt.cli;

import static io.dangernoodle.grt.Constants.UPDATE_REF;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.Injector;

import io.dangernoodle.grt.util.CommandArguments.Ref;
import io.dangernoodle.grt.util.CommandArguments.Sha1orTag;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;


@Command(name = UPDATE_REF)
public class UpdateRefCommand extends io.dangernoodle.grt.Command.Definition.Only
{
    @Mixin
    private Ref ref;

    @ArgGroup(exclusive = true, multiplicity = "1")
    private Sha1orTag sha1orTag;

    public UpdateRefCommand(Injector injector)
    {
        super(injector);
    }

    @Override
    public Map<Object, Object> toArgMap()
    {
        HashMap<Object, Object> args = new HashMap<>(super.toArgMap());
        args.putAll(ref.toArgMap());
        args.putAll(sha1orTag.toArgMap());

        return args;
    }
}
