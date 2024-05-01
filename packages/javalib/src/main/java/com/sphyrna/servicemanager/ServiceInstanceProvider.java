package com.sphyrna.servicemanager;

/**
 * A ServiceInstanceProvider creates a single instance of a service.
 */
public interface ServiceInstanceProvider<S extends Service>
{
    /**
     * Create a Service instance
     * @throws ServiceException
     */
    S createServiceInstance() throws ServiceException;
}
