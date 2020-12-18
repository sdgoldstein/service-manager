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

    public Service getService(String name) throws ServiceException
    {
        if (name == null)
        {
            throw new IllegalArgumentException("name cannot be null.");
        }

        if (!SERVICE_DEFINITIONS.containsKey(name))
        {
            throw new ServiceNotAvailableException(name);
        }

        ServiceController serviceLiason;

        // Check if the service is already active FIXME - This means that all services are singletons.  shouldn't be
        if (CONTROLLERS_FOR_ACTIVE_SERVICES.containsKey(name))
        {
            serviceLiason = CONTROLLERS_FOR_ACTIVE_SERVICES.get(name);
        }
        else
        {
            // Create and start it (NOT THREAD SAFE FIXME)
            ServiceDefinition serviceDefinition = SERVICE_DEFINITIONS.get(name);

            Configuration serviceConfiguration = serviceDefinition.getServiceConfiguration();
            serviceLiason = serviceDefinition.getServiceLiason();

            serviceLiason.initService(serviceConfiguration);
            serviceLiason.startService();
            CONTROLLERS_FOR_ACTIVE_SERVICES.put(name, serviceLiason);
        }

        return serviceLiason.getService();
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

        ServiceDefinition serviceDefinition = new ServiceDefinitionByClass(serviceProviderClass,
                                                                    configuration);
        SERVICE_DEFINITIONS.put(name, serviceDefinition);
    }

    @Override
    public void defineService(String name, ServiceProvider serviceProvider) throws ServiceException
    {
        if (name == null)
        {
            throw new IllegalArgumentException("name cannot be null.");
        }

        if (serviceProvider == null)
        {
            throw new IllegalArgumentException("serviceProvider cannot be null.");
        }

        if (SERVICE_DEFINITIONS.containsKey(name))
        {
            StringBuffer errorMessage = new StringBuffer("Service with name, ");
            errorMessage.append(name);
            errorMessage.append(", already exists.");
            throw new IllegalStateException(errorMessage.toString());
        }

        ServiceDefinition serviceDefinition = new ServiceDefinitionByInstance(serviceProvider);

        SERVICE_DEFINITIONS.put(name, serviceDefinition);
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

    private abstract class ServiceDefinition
    {
        protected Configuration serviceConfiguration;

        private ServiceDefinition(Configuration serviceConfiguration)
        {
            this.serviceConfiguration = serviceConfiguration;
        }

        private Configuration getServiceConfiguration()
        {
            return this.serviceConfiguration;
        }

        public abstract ServiceController getServiceLiason() throws ServiceException;
    }

    private class ServiceDefinitionByClass extends ServiceDefinition
    {
        private Class<? extends ServiceProvider> serviceProviderClass;

        private ServiceDefinitionByClass(
                                  Class<? extends ServiceProvider> serviceProviderClass,
                                  Configuration serviceConfiguration)
        {
            super(serviceConfiguration);
            this.serviceProviderClass = serviceProviderClass;
        }

        @Override
        public ServiceController getServiceLiason() throws ServiceException
        {
            ServiceController serviceLiasonToReturn = null;

            try
            {
                ServiceProvider serviceProvider = this.serviceProviderClass.newInstance();
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
    }

    private class ServiceDefinitionByInstance extends ServiceDefinition
    {
        private ServiceProvider serviceProvider;

        public ServiceDefinitionByInstance(ServiceProvider serviceProvider)
        {
            super(EMPTY_CONFIGURATION);
            this.serviceProvider = serviceProvider;
        }

        @Override
        public ServiceController getServiceLiason() throws ServiceException
        {
            return this.serviceProvider.createService();
        }
    }
}
