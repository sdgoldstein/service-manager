package com.sphyrna.servicemanager;

import java.util.Objects;

/**
 * InvalidServiceDefinitionException is thrown when a service definition is not
 * valid according to the Service API rules
 */
public class InvalidServiceDefinitionException extends ServiceException
{

    public InvalidServiceDefinitionException(String message)
    {
        super(Objects.requireNonNull(message, "message cannot be null"));
    }

    public InvalidServiceDefinitionException(String message, Throwable cause)
    {
        super(Objects.requireNonNull(message, "message cannot be null"),
              Objects.requireNonNull(cause, "cause cannot be null"));
    }

    public InvalidServiceDefinitionException(Throwable cause)
    {
        super(Objects.requireNonNull(cause, "cause cannot be null"));
    }
}
