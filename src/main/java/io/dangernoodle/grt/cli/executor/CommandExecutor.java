package io.dangernoodle.grt.cli.executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.dangernoodle.grt.Command;

/**
 * @since 0.9.0
 */
public abstract class CommandExecutor
{
    protected final Logger logger;

    public CommandExecutor()
    {
        this.logger = LoggerFactory.getLogger(getClass());
    }

    public abstract void execute(Command command) throws Exception;
}