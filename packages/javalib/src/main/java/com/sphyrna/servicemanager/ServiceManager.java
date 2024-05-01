package com.sphyrna.servicemanager;

import com.sphyrna.servicemanager.providers.ConfigServiceManagerStrategy;

/**
 * The ServiceManager is used to manage and retrieve services
 *
 * @author sgoldstein
 *
 */
public class ServiceManager
{
    private static ServiceManagerStrategy serviceManagerStrategy = new ConfigServiceManagerStrategy();

    /**
     * Retrieve a service
     *
     * @param name
     *            the name of the service
     * @return the service associated with the specified name
     * @throws ServiceNotAvailableException
     *             if the specified service does not exist
     */
    public static <S extends Service<C>, C extends ServiceConfiguration> S getService(String name)
        throws ServiceNotAvailableException, ServiceException
    {
        return serviceManagerStrategy.getService(name);
    }

    /**
     * Determine if a service with the specified name is defined
     *
     * @param name
     *            the name of the service to investigate
     * @return true if the service is available; false otherwise
     */
    public static boolean isServiceDefined(String name)
    {
        return serviceManagerStrategy.isServiceDefined(name);
    }

    /**
     * Shutdown the ServiceManager and all associated services
     */
    public static void shutdown()
    {
        serviceManagerStrategy.shutdown();
    }

    /**
     * Set the service manager strategy.  This strategy will be used to retrieve services based on the algorithm defined
     * in the ServiceManagerFactory implementnatation
     *
     * In the future, a ServiceManager may be able to use multiple strategies concurrently.  As of the current API, it
     * only supports a single strategy
     *
     * @param strategyToSet the service manager strategy to set
     */
    public static void setDefaultStrategy(ServiceManagerStrategy strategyToSet)
    {
        serviceManagerStrategy = strategyToSet;
    }
}
