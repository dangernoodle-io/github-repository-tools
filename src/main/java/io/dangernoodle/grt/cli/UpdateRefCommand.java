package io.dangernoodle.grt.cli;

import static io.dangernoodle.grt.Constants.UPDATE_REF;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.Injector;

import io.dangernoodle.grt.cli.options.ReferenceOption;
import io.dangernoodle.grt.cli.options.Sha1OrTagOption;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;


@Command(name = UPDATE_REF)
public class UpdateRefCommand extends DefinitionOnlyCommand
{
    @Mixin
    private ReferenceOption ref;

    @ArgGroup(exclusive = true, multiplicity = "1")
    private Sha1OrTagOption sha1orTag;

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
