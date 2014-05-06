package com.lifeease.pim.service.defaultimpl;

import com.lifeease.pim.service.Configuration;
import com.lifeease.pim.service.MapConfiguration;
import com.lifeease.pim.service.Service;
import com.lifeease.pim.service.ServiceController;
import com.lifeease.pim.service.ServiceException;
import com.lifeease.pim.service.ServiceManager;
import com.lifeease.pim.service.ServiceNotAvailableException;
import com.lifeease.pim.service.ServiceProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Default implementation of the Service Manager interface
 * 
 * @author sgoldstein
 * 
 */
public class ServiceManagerImpl implements ServiceManager
{
    private static Log LOG = LogFactory.getLog(ServiceManagerImpl.class.getName());

    private static final Configuration EMPTY_CONFIGURATION = new MapConfiguration();

    // Currently, all services are singletons
    private static final Map<String, ServiceController> CONTROLLERS_FOR_ACTIVE_SERVICES = new HashMap<String, ServiceController>();
    private static final Map<String, ServiceDefinition> SERVICE_DEFINITIONS = new HashMap<String, ServiceDefinition>();

    public Service getService(String name)
    {
        if (name == null)
        {
            throw new IllegalArgumentException("name cannot be null.");
        }

        if (!CONTROLLERS_FOR_ACTIVE_SERVICES.containsKey(name))
        {
            throw new ServiceNotAvailableException(name);
        }

        return CONTROLLERS_FOR_ACTIVE_SERVICES.get(name).getService();
    }

    public boolean isServiceAvailable(String name)
    {
        if (name == null)
        {
            throw new IllegalArgumentException("name cannot be null.");
        }

        return SERVICE_DEFINITIONS.containsKey(name);
    }

    public void defineService(String name,
                              Class<? extends ServiceProvider> serviceClass)
        throws ServiceException
    {
        defineService(name, serviceClass, EMPTY_CONFIGURATION);
    }

    public void defineService(String name,
                              Class<? extends ServiceProvider> serviceProviderClass,
                              Configuration configuration)
        throws ServiceException
    {
        if (name == null)
        {
            throw new IllegalArgumentException("name cannot be null.");
        }

        if (serviceProviderClass == null)
        {
            throw new IllegalArgumentException("serviceProviderClass cannot be null.");
        }

        if (configuration == null)
        {
            throw new IllegalArgumentException("configuration cannot be null.");
        }

        if (SERVICE_DEFINITIONS.containsKey(name))
        {
            StringBuffer errorMessage = new StringBuffer("Service with name, ");
            errorMessage.append(name);
            errorMessage.append(", already exists.");
            throw new IllegalStateException(errorMessage.toString());
        }

        ServiceDefinition serviceDefinition = new ServiceDefinition(serviceProviderClass,
                                                                    configuration);
        SERVICE_DEFINITIONS.put(name, serviceDefinition);

        ServiceController serviceLiason = getServiceLiason(serviceProviderClass);
        CONTROLLERS_FOR_ACTIVE_SERVICES.put(name, serviceLiason);
        serviceLiason.initService(configuration);
        serviceLiason.startService();
        CONTROLLERS_FOR_ACTIVE_SERVICES.put(name, serviceLiason);
    }

    private ServiceController getServiceLiason(Class<? extends ServiceProvider> serviceProviderClass)
        throws ServiceException
    {
        ServiceController serviceLiasonToReturn = null;

        try
        {
            ServiceProvider serviceProvider = serviceProviderClass.newInstance();
            serviceLiasonToReturn = serviceProvider.createService();
        }
        catch (IllegalAccessException exception)
        {
            StringBuffer errorMessage = new StringBuffer("Service provider class, ");
            errorMessage.append(serviceProviderClass.getName());
            errorMessage.append(", does not have a public, no arg constructor.");

            throw new ServiceException(errorMessage.toString(), exception);
        }
        catch (InstantiationException exception)
        {
            StringBuffer errorMessage = new StringBuffer("Failure instantiating Service Provider Class, ");
            errorMessage.append(serviceProviderClass.getName());
            errorMessage.append(".  No arg constructor threw exception.");

            throw new ServiceException(errorMessage.toString(), exception);
        }

        return serviceLiasonToReturn;
    }

    public void shutdown()
    {
        // FIX ME - What about inactive (or stopped) services
        Iterator<Entry<String, ServiceController>> serviceLiasonIterator = CONTROLLERS_FOR_ACTIVE_SERVICES.entrySet()
                                                                                                          .iterator();
        while (serviceLiasonIterator.hasNext())
        {
            Entry<String, ServiceController> nextServiceControllerEntry = serviceLiasonIterator.next();
            ServiceController nextServiceController = nextServiceControllerEntry.getValue();
            try
            {
                nextServiceController.stopService();
                nextServiceController.destroyService();
            }
            catch (Throwable throwable)
            {
                LOG.error("Failed to stop service with name, "
                          + nextServiceControllerEntry.getKey(), throwable);
            }
        }

        CONTROLLERS_FOR_ACTIVE_SERVICES.clear();
        SERVICE_DEFINITIONS.clear();
    }

    private class ServiceDefinition
    {
        private Class<? extends ServiceProvider> serviceProviderClass;
        private Configuration serviceConfiguration;

        private ServiceDefinition(
                                  Class<? extends ServiceProvider> serviceProviderClass,
                                  Configuration serviceConfiguration)
        {
            this.serviceProviderClass = serviceProviderClass;
            this.serviceConfiguration = serviceConfiguration;
        }

        private Class<? extends ServiceProvider> getServiceProviderClass()
        {
            return this.serviceProviderClass;
        }

        private Configuration getServiceConfiguration()
        {
            return this.serviceConfiguration;
        }
    }
}
