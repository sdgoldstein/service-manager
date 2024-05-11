package com.sphyrna.servicemanager;

/**
 * Defines a Service Manager Strategy that controls the behavior of the ServiceManager
 */
public interface ServiceManagerStrategy
{
    /**
     * Retrieve a service
     *
     * @param name the name of the service
     * @return the requested service if exists
     */
    public <S extends Service<ServiceConfiguration>> S getService(String name) throws ServiceException;

    /**
     * Retrieve a service
     *
     * @param name the name of the service
     * @param serviceConfiguration service instance level configuration.  This must be supported by the associated
     *     ServiceLifecycleManager
     * @return the requested service if exists
     */
    public <S extends Service<C>, C extends ServiceConfiguration> S getService(String name, C serviceConfiguration)
        throws ServiceException;

    /**
     * Determine if a service with the specified name has been defined
     *
     * @param name the name of the service
     * @return true if the service is defined, false otherwise
     */
    boolean isServiceDefined(String name);

    /**
     * Shutdown the ServiceManager and all associated services
     */
    void shutdown();
}
