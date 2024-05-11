package com.sphyrna.servicemanager.defaultimpl;

import com.sphyrna.servicemanager.*;
import com.sphyrna.servicemanager.providers.KnownServiceLifecycleControllers;
import com.sphyrna.servicemanager.providers.MapConfiguration;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Default implementation of the Service Manager interface
 *
 * @author sgoldstein
 */
public class ServiceManagerStrategyImpl implements ServiceManagerStrategy
{
    private static final Log LOG = LogFactory.getLog(ServiceManagerStrategyImpl.class.getName());

    private final Map<String, ServiceLifecycleController<?, ?>> CONTROLLERS_FOR_ACTIVE_SERVICES =
        new HashMap<String, ServiceLifecycleController<?, ?>>();
    private final Map<String, ServiceDefinition<?, ?>> SERVICE_DEFINITIONS =
        new HashMap<String, ServiceDefinition<?, ?>>();

    @Override
    public <S extends Service> S getService(String name) throws ServiceException
    {
        return this.<S, ServiceConfiguration>getService(name, ServiceManagerConstants.EMPTY_SERVICE_CONFIGURATION);
    }

    @Override
    public <S extends Service<C>, C extends ServiceConfiguration> S getService(String name, C serviceConfiguration)
        throws ServiceException
    {
        if (name == null)
        {
            throw new IllegalArgumentException("name cannot be null.");
        }

        if (!SERVICE_DEFINITIONS.containsKey(name))
        {
            throw new ServiceNotAvailableException(name);
        }

        ServiceLifecycleController<S, C> serviceLifecycleController;

        // Check if the service lifecycle controller has already been initialized
        if (CONTROLLERS_FOR_ACTIVE_SERVICES.containsKey(name))
        {
            serviceLifecycleController = (ServiceLifecycleController<S, C>)CONTROLLERS_FOR_ACTIVE_SERVICES.get(name);
        }
        else
        {
            // Create and start it
            // FIXME - Not Thread Safe
            ServiceDefinition<S, C> serviceDefinition = (ServiceDefinition<S, C>)SERVICE_DEFINITIONS.get(name);
            serviceLifecycleController = serviceDefinition.getServiceLifecycleController();

            if (serviceDefinition.hasServiceInstanceProvider())
            {
                serviceLifecycleController.init(serviceDefinition.getServiceInstanceProvider(),
                                                serviceDefinition.getServiceConfiguration());
            }

            CONTROLLERS_FOR_ACTIVE_SERVICES.put(name, serviceLifecycleController);
        }

        return serviceLifecycleController.getService();
    }

    @Override
    public boolean isServiceDefined(String name)
    {
        if (name == null)
        {
            throw new IllegalArgumentException("name cannot be null.");
        }

        return SERVICE_DEFINITIONS.containsKey(name);
    }

    public <S extends Service<C>, C extends ServiceConfiguration> void
    registerService(String name, ServiceInstanceProvider<S> serviceInstanceProvider,
                    ServiceLifecycleController<S, C> serviceLifecycleController, C serviceConfiguration)
        throws ServiceException
    {
        this.registerService(name, serviceInstanceProvider, serviceLifecycleController, serviceConfiguration, false);
    }

    public <S extends Service<C>, C extends ServiceConfiguration> void
    registerService(String name, ServiceInstanceProvider<S> serviceInstanceProvider,
                    ServiceLifecycleController<S, C> serviceLifecycleController, C serviceConfiguration,
                    boolean override) throws ServiceException
    {
        if (name == null)
        {
            throw new IllegalArgumentException("name cannot be null.");
        }

        if (serviceInstanceProvider == null)
        {
            throw new IllegalArgumentException("serviceProvider cannot be null.");
        }

        if (SERVICE_DEFINITIONS.containsKey(name))
        {
            this.handleServiceOverride(name, override);
        }

        ServiceDefinition<S, C> serviceDefinition =
            new ServiceDefinition<S, C>(serviceLifecycleController, serviceInstanceProvider, serviceConfiguration);

        this.SERVICE_DEFINITIONS.put(name, serviceDefinition);
    }

    public <S extends Service<C>, C extends ServiceConfiguration> void
    registerServiceByControllerOnly(String name, ServiceLifecycleController<S, C> serviceLifecycleController,
                                    C serviceConfiguration)
    {
        this.registerServiceByControllerOnly(name, serviceLifecycleController, serviceConfiguration, false);
    }

    /**
     * Register a service.  This is the minimal number of inputs needed to register a new service.
     *
     * IMPORTANT NOTE: If registering a lifecycle controller through this method, the init(serviceInstanceProvider:
     * ServiceInstanceProvider<T>, config: C) method will NOT be called.  If it must be called, it should be invoked by
     * the client before registering the service
     *
     * @param name
     *            the name of the defined service
     * @param serviceLifecycleController
     *            controls the service lifeycle. For known controllers,
     * see {@KnownServiceLifecycleControllers}
     * @param override
     *            boolean indicating whether or not to override an
     * existing defintion with the same name. If this is false and a service
     * definition already exists, an error will be thrown
     */
    public <S extends Service<C>, C extends ServiceConfiguration> void
    registerServiceByControllerOnly(String name, ServiceLifecycleController<S, C> serviceLifecycleController,
                                    C serviceConfiguration, boolean override)
    {
        if (this.SERVICE_DEFINITIONS.containsKey(name))
        {
            this.handleServiceOverride(name, override);
        }

        ServiceDefinition<S, C> serviceDefinition =
            new ServiceDefinition<S, C>(serviceLifecycleController, serviceConfiguration);

        this.SERVICE_DEFINITIONS.put(name, serviceDefinition);
    }

    public void registerServiceByClass(String name, Class<? extends Service> serviceClass,
                                       Class<? extends ServiceLifecycleController> serviceLifecycleControllerClass,
                                       ServiceConfiguration serviceConfiguration) throws ServiceException
    {
        this.registerServiceByClass(name, serviceClass, serviceLifecycleControllerClass, serviceConfiguration, false);
    }

    /**
     * Register a service providing a contructor method for a service class and service lifecycle controller class
     *
     * @param name
     *            the name of the defined service
     * @param serviceClass
     *            the class that implements the service
     * @param serviceLifecycleControllerClass
     *            the class that controls the service lifeycle. For known controllers,
     * see {@KnownServiceLifecycleControllers}
     * @param config
     *            An Optional config may be supplied for service definition level
     * configuration.
     * @param override
     *            An optional boolean indicating whether or not to override an
     * existing defintion with the same name. If this is false and a service
     * definition already exists, an error will be thrown
     */
    public void registerServiceByClass(String name, Class<? extends Service> serviceClass,
                                       Class<? extends ServiceLifecycleController> serviceLifecycleControllerClass,
                                       ServiceConfiguration serviceConfiguration, boolean override)
        throws ServiceException
    {
        ServiceInstanceProvider serviceProvider = new ServiceInstanceProviderImpl(serviceClass);
        ServiceLifecycleController serviceLifecycleController;
        try
        {
            serviceLifecycleController = serviceLifecycleControllerClass.getDeclaredConstructor().newInstance();
        }
        catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException |
               NoSuchMethodException | SecurityException exception)
        {
            // TODO Auto-generated catch block
            throw new ServiceException("Failed to instantiate service lifecycle controller", exception);
        }
        this.registerService(name, serviceProvider, serviceLifecycleController, serviceConfiguration, override);
    }

    public void registerSingletonService(String name, Class<? extends Service> serviceClass,
                                         ServiceConfiguration serviceConfiguration) throws ServiceException
    {
        this.registerSingletonService(name, serviceClass, serviceConfiguration, false);
    }

    /**
     * Register a service that has a singleton lifecycle (only one instance exists)
     *
     * @param name
     *            the name of the defined service
     * @param serviceClass
     *            the class that implements the service
     * @param config
     *            An Optional config may be supplied for service definition level
     * configuration.
     * @param override
     *            An optional boolean indicating whether or not to override an
     * existing defintion with the same name. If this is false and a service
     * definition already exists, an error will be thrown
     */
    public void registerSingletonService(String name, Class<? extends Service> serviceClass,
                                         ServiceConfiguration serviceConfiguration, boolean override)
        throws ServiceException
    {
        this.registerServiceByClass(name, serviceClass, KnownServiceLifecycleControllers.SINGLETON,
                                    serviceConfiguration, override);
    }

    private void handleServiceOverride(String name, boolean override)
    {
        if (override)
        {
            // This is an override.  Remove defintions.  Shutdown lifecycle controller
            this.SERVICE_DEFINITIONS.remove(name);
            if (this.CONTROLLERS_FOR_ACTIVE_SERVICES.containsKey(name))
            {
                ServiceLifecycleController<?, ?> controllerToRemove = this.CONTROLLERS_FOR_ACTIVE_SERVICES.remove(name);
                controllerToRemove.shutdown();
            }
        }
        else
        {
            throw new IllegalArgumentException("Service with name, " + name +
                                               ", is already defined.  Specify override=true if it should be replaced");
        }
    }

    public void shutdown()
    {
        // FIX ME - What about inactive (or stopped) services
        Iterator<Entry<String, ServiceLifecycleController<?, ?>>> serviceLiasonIterator =
            this.CONTROLLERS_FOR_ACTIVE_SERVICES.entrySet().iterator();
        while (serviceLiasonIterator.hasNext())
        {
            Entry<String, ServiceLifecycleController<?, ?>> nextServiceControllerEntry = serviceLiasonIterator.next();
            ServiceLifecycleController<?, ?> nextServiceController = nextServiceControllerEntry.getValue();
            try
            {
                nextServiceController.shutdown();
            }
            catch (Throwable throwable)
            {
                LOG.error("Failed to stop service with name, " + nextServiceControllerEntry.getKey(), throwable);
            }
        }

        CONTROLLERS_FOR_ACTIVE_SERVICES.clear();
        SERVICE_DEFINITIONS.clear();
    }

    private class ServiceDefinition<S extends Service<C>, C extends ServiceConfiguration>
    {
        private final ServiceInstanceProvider<S> serviceInstanceProvider;
        private final C serviceConfiguration;
        private final ServiceLifecycleController<S, C> serviceLifecycleController;

        private ServiceDefinition(ServiceLifecycleController<S, C> serviceLifecycleController, C config)
        {
            this.serviceLifecycleController = serviceLifecycleController;
            this.serviceConfiguration = config;
            this.serviceInstanceProvider = null;
        }

        private ServiceDefinition(ServiceLifecycleController<S, C> serviceLifecycleController,
                                  ServiceInstanceProvider<S> serviceInstanceProvider, C config)
        {
            this.serviceLifecycleController = serviceLifecycleController;
            this.serviceInstanceProvider = serviceInstanceProvider;
            this.serviceConfiguration = config;
        }

        private boolean hasServiceInstanceProvider()
        {
            return (this.serviceInstanceProvider != null);
        }

        private ServiceInstanceProvider<S> getServiceInstanceProvider()
        {
            if (this.serviceInstanceProvider == null)
            {
                throw new IllegalStateException("serviceInstanceProvider called when one doesn't exist");
            }

            return this.serviceInstanceProvider;
        }

        private C getServiceConfiguration()
        {
            return this.serviceConfiguration;
        }

        private ServiceLifecycleController<S, C> getServiceLifecycleController()
        {
            return this.serviceLifecycleController;
        }
    }
}
