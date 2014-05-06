package com.lifeease.pim.service;

/**
 * ServiceProvide is implemented to provide a particular
 * 
 * @author sgoldstein
 * 
 */
public interface ServiceProvider
{
    /**
     * Create a Service instance
     * 
     * @return a ServiceLiaison which is used to control the created service
     * @throws ServiceException
     *             if a failure occurs when creating service
     */
    public ServiceController createService() throws ServiceException;
}
