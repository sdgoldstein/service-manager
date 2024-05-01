package com.sphyrna.servicemanager;

/**
 * Thrown by a Service when a failed attempt is made to execute one of the
 * service's lifecycle methods
 *
 */
public class ServiceException extends Exception
{
    public ServiceException(String message)
    {
        super(message);

        if (message == null)
        {
            throw new IllegalArgumentException("message cannot be null");
        }
    }

    public ServiceException(String message, Throwable cause)
    {
        super(message, cause);

        if (message == null)
        {
            throw new IllegalArgumentException("message cannot be null");
        }

        if (cause == null)
        {
            throw new IllegalArgumentException("cause cannot be null");
        }
    }

    public ServiceException(Throwable cause)
    {
        super(cause);

        if (cause == null)
        {
            throw new IllegalArgumentException("cause cannot be null");
        }
    }
}
