package com.lifeease.pim.service;

/**
 * ServiceController is responsible for provide a control proxy to the Service.
 * It's invoked by the ServiceManager to create, initialize, start, stop, and
 * destroy a Service
 * 
 * @author sgoldstein
 * 
 */
public interface ServiceController
{
    /**
     * Initialize the service.
     * 
     * @throws ServiceException
     *             if initialization fails
     */
    public void initService(Configuration configuration)
        throws ServiceException;

    /**
     * Start the service.
     * 
     * @throws ServiceException
     *             if the service fails to start
     * 
     */
    public void startService() throws ServiceException;

    /**
     * Stop the service.
     * 
     * @throws ServiceException
     *             if the service fails to stop
     * 
     */
    public void stopService() throws ServiceException;

    /**
     * Destroy the service and clean up resources
     * 
     * @throws ServiceException
     *             if destruction fails
     */
    public void destroyService() throws ServiceException;

    /**
     * Retrieve the wrapped Service
     * 
     * @return the wrapped Service
     */
    public Service getService();
}
