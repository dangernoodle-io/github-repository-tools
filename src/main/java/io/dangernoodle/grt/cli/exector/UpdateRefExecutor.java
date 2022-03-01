package io.dangernoodle.grt.cli.exector;

import static io.dangernoodle.grt.Constants.REF;
import static io.dangernoodle.grt.Constants.SHA1;
import static io.dangernoodle.grt.Constants.TAG;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import io.dangernoodle.grt.Command;
import io.dangernoodle.grt.Workflow;
import io.dangernoodle.grt.cli.UpdateRefCommand;


/**
 * @since 0.9.0
 */
public class UpdateRefExecutor extends DefinitionExecutor
{
    public UpdateRefExecutor(Path definitionRoot, Workflow<Path> workflow)
    {
        super(definitionRoot, workflow);
    }

    @Override
    protected Map<Object, Object> getArguments(Command command)
    {
        UpdateRefCommand updateRef = (UpdateRefCommand) command;

        // Map.of doesn't allow null values
        HashMap<Object, Object> args = new HashMap<>();
        args.put(REF, updateRef.getRef());
        args.put(SHA1, updateRef.getSha1());
        args.put(TAG, updateRef.getTag());

        return args;
    }
}
