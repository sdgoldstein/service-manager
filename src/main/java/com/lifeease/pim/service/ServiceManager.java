package com.lifeease.pim.service;

/**
 * The ServiceManager is used to manage and retrieve services
 * 
 * @author sgoldstein
 * 
 */
public interface ServiceManager
{
    /**
     * Retrieve a service
     * 
     * @param name
     *            the name of the service
     * @return the service associated with the specified name
     * @throws ServiceNotAvailableException
     *             if the specified service does not exist
     */
    public Service getService(String name) throws ServiceNotAvailableException;

    /**
     * Determine if a service with the specified name is available
     * 
     * @param name
     *            the name of the service to investigate
     * @return true if the service is available; false otherwise
     */
    public boolean isServiceAvailable(String name);

    /**
     * Define a service
     * 
     * @param name
     *            the name of the defined service
     * @param serviceClass
     *            the class of the service
     * @return the defined service, initiated and started FIX ME - Is this
     *         correct?
     * @throws ServiceException
     *             if a failure occurs initializing the service
     */
    public void defineService(String name, Class<? extends ServiceProvider> serviceClass)
        throws ServiceException;

    /**
     * Define a service, providing configuration for the service *
     * 
     * @param name
     *            the name of the defined service
     * @param serviceClass
     *            the class of the service
     * @return the defined service, initiated and started FIX ME - Is this
     *         correct?
     * @param configuration
     *            configuration parameters for the service
     * @return the defined service, initiated and started FIX ME - Is this
     *         correct?
     * @throws ServiceException
     *             if a failure occurs initializing the service
     */
    public void defineService(String name,
                                 Class<? extends ServiceProvider> serviceClass,
                                 Configuration configuration)
        throws ServiceException;

    /**
     * Shutdown the ServiceManager and all associated services
     */
    public void shutdown();
}
