package com.sphyrna.servicemanager;

/**
 * Thrown when a requested service is not available
 */
public class ServiceNotAvailableException extends RuntimeException
{
    private String badServiceName;

    public ServiceNotAvailableException(String badServiceName)
    {
        super(buildMessage(badServiceName));

        if (badServiceName == null)
        {
            throw new IllegalArgumentException("badServiceName cannot be null");
        }

        this.badServiceName = badServiceName;
    }

    public String getBadServiceName()
    {
        return this.badServiceName;
    }

    private static String buildMessage(String badServiceName)
    {
        if (badServiceName == null)
        {
            throw new IllegalArgumentException("badServiceName cannot be null");
        }

        StringBuffer messageBuffer = new StringBuffer("A Service with name, ");
        messageBuffer.append(badServiceName);
        messageBuffer.append(", could not be found.");

        return messageBuffer.toString();
    }
}
