package io.dangernoodle.grt.util;

import java.nio.file.Path;

import io.dangernoodle.grt.Workflow;

/**
 * @since 0.9.0
 */
public class PathToXConverter<X> implements Workflow<Path>
{
    private final Converter<X> converter;

    private final Workflow<X> workflow;

    public PathToXConverter(Workflow<X> workflow, Converter<X> converter)
    {
        this.converter = converter;
        this.workflow = workflow;
    }

    @Override
    public void execute(Path object, Context context) throws Exception
    {
        workflow.execute(converter.convert(object), context);
    }

    @Override
    public void postExecution()
    {
        workflow.postExecution();
    }

    @Override
    public void preExecution() throws Exception
    {
        workflow.preExecution();
    }

    @FunctionalInterface
    public static interface Converter<X>
    {
        X convert(Path path) throws Exception;
    }
}
