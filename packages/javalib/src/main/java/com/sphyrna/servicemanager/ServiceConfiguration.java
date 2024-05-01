package com.sphyrna.servicemanager;

/**
 * ServiceConfiguration is an interface representing the configuration properties
 * provided to a service
 *
 */
public interface ServiceConfiguration
{
    /**
     * Retrieve a configuration parameter by name
     *
     * @param name
     *            The name of the configuration property to retrieve
     * @return the value of the configuration property associated with the
     *         specified name
     * @throws IllegalArgumentException
     *             if a property with the specified name was not provided
     */
    public Object getProperty(String name);

    /**
     * Determine if a configuration properties with the specified name was
     * provided
     *
     * @param name
     *            The name of the property to investigate
     * @return true if the property was provided; false otherwise
     */
    public boolean containsProperty(String name);
}
