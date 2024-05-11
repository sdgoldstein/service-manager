package com.sphyrna.servicemanager;

import java.util.Objects;

/**
 * Thrown when a requested service is not available
 */
public class ServiceNotAvailableException extends RuntimeException
{
    private String badServiceName;

    public ServiceNotAvailableException(String badServiceName)
    {
        super(buildMessage(badServiceName));

        this.badServiceName = Objects.requireNonNull(badServiceName, "badServiceName cannot be null");
    }

    public String getBadServiceName()
    {
        return this.badServiceName;
    }

    private static String buildMessage(String badServiceName)
    {
        Objects.requireNonNull(badServiceName, "badServiceName cannot be null");

        StringBuffer messageBuffer = new StringBuffer("A Service with name, ");
        messageBuffer.append(badServiceName);
        messageBuffer.append(", could not be found.");

        return messageBuffer.toString();
    }
}
