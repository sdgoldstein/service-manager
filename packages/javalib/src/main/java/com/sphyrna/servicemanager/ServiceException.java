package com.sphyrna.servicemanager;

import java.util.Objects;

/**
 * Thrown by a Service when a failed attempt is made to execute one of the
 * service's lifecycle methods
 *
 */
public class ServiceException extends Exception
{
    public ServiceException(String message)
    {
        super(Objects.requireNonNull(message, "message cannot be null"));
    }

    public ServiceException(String message, Throwable cause)
    {
        super(Objects.requireNonNull(message, "message cannot be null"),
              Objects.requireNonNull(cause, "cause cannot be null"));
    }

    public ServiceException(Throwable cause)
    {
        super(Objects.requireNonNull(cause, "cause cannot be null"));
    }
}
