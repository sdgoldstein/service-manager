package com.sphyrna.servicemanager;

/**
 * InvalidServiceDefinitionException is thrown when a service definition is not
 * valid according to the Service API rules
 */
public class InvalidServiceDefinitionException extends ServiceException
{

    public InvalidServiceDefinitionException(String message)
    {
        super(message);

        if (message == null)
        {
            throw new IllegalArgumentException("message cannot be null");
        }
    }

    public InvalidServiceDefinitionException(String message, Throwable cause)
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

    public InvalidServiceDefinitionException(Throwable cause)
    {
        super(cause);

        if (cause == null)
        {
            throw new IllegalArgumentException("cause cannot be null");
        }
    }
}
