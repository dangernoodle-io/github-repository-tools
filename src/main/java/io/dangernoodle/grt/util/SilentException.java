package io.dangernoodle.grt.util;

/**
 * Exceptions thrown using this class will be swallowed and have their console output suppressed while still causing a
 * <code>System.exit</code> to occur.
 * 
 * @since 0.10.0
 */
public class SilentException extends RuntimeException
{
    private static final long serialVersionUID = 8148265305642800360L;

    public SilentException(String message)
    {
        super(message);
    }
}
