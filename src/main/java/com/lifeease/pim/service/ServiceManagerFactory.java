package com.lifeease.pim.service;

import com.lifeease.pim.service.defaultimpl.ServiceManagerImpl;

/**
 * Factory used to retrieve the ServiceManager
 * 
 * @author sgoldstein
 * 
 */
public class ServiceManagerFactory
{
    private static final ServiceManagerFactory SINGLETON_INSTANCE = new ServiceManagerFactory();
    private static final ServiceManager SINGLETON_SERVICE_MANAGER = new ServiceManagerImpl();

    private ServiceManagerFactory()
    {

    }

    /**
     * Retrieve the instance of the ServiceManagerFactory
     * 
     * @return the instance of the ServiceManagerFactory
     */
    public static ServiceManagerFactory getInstance()
    {
        return SINGLETON_INSTANCE;
    }

    /**
     * Retrieve the instance of the ServiceManager
     * 
     * @return the instance of the ServiceManager
     */
    public ServiceManager getServiceManager()
    {
        return SINGLETON_SERVICE_MANAGER;
    }
}
