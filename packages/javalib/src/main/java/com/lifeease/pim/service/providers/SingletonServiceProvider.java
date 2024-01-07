package com.lifeease.pim.service.providers;

import com.lifeease.pim.service.*;

import java.util.HashMap;
import java.util.Map;

public class SingletonServiceProvider implements ServiceProvider
{
    private Class<? extends AbstractService> serviceClass;

    private SingletonServiceProvider()
    {
    }

    private SingletonServiceProvider(Class<? extends AbstractService> serviceClass)
    {
        if (serviceClass == null)
        {
            throw new IllegalArgumentException("service cannot be null");
        }

        this.serviceClass = serviceClass;
    }

    public static SingletonServiceProvider createSingletonSeviceProvider(Class<? extends AbstractService> serviceClass)
    {
        if (serviceClass == null)
        {
            throw new IllegalArgumentException("service cannot be null");
        }

        SingletonServiceProvider serviceProvider = new SingletonServiceProvider(serviceClass);
        return serviceProvider;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.lifeease.pim.service.ServiceProvider#createService()
     */
    @Override
    public ServiceController createService() throws ServiceException
    {
        AbstractService createdService = null;
        try
        {
            createdService = this.serviceClass.newInstance();
        }
        catch (InstantiationException exception)
        {
            throw new ServiceException("Failed to instantiate service",
                    exception);
        }
        catch (IllegalAccessException exception)
        {
            throw new ServiceException("Failed to instantiate service",
                    exception);
        }

        return new ServiceControllerImpl(createdService);
    }

    private class ServiceControllerImpl implements ServiceController
    {
        // FIXME This should not require an abstract service.  Need a Service interface extension that indicates a service can be controlled
        private AbstractService service;

        /**
         * @param service
         */
        public ServiceControllerImpl(AbstractService service)
        {
            if (service == null)
            {
                throw new IllegalArgumentException("service cannot be null");
            }
            this.service = service;
        }

        /*
         * (non-Javadoc)
         *
         * @see com.lifeease.pim.service.ServiceLiaison#destroyService()
         */
        @Override
        public void destroyService() throws ServiceException
        {
            this.service.destroy();
        }

        /*
         * (non-Javadoc)
         *
         * @see com.lifeease.pim.service.ServiceLiaison#getService()
         */
        @Override
        public Service getService()
        {
            return this.service;
        }

        /*
         * (non-Javadoc)
         *
         * @see com.lifeease.pim.service.ServiceLiaison#initService(com.lifeease.pim.service.Configuration)
         */
        @Override
        public void initService(Configuration configuration)
                throws ServiceException
        {
            this.service.init(configuration);
        }

        /*
         * (non-Javadoc)
         *
         * @see com.lifeease.pim.service.ServiceLiaison#startService()
         */
        @Override
        public void startService() throws ServiceException
        {
            this.service.start();

        }

        /*
         * (non-Javadoc)
         *
         * @see com.lifeease.pim.service.ServiceLiaison#stopService()
         */
        @Override
        public void stopService() throws ServiceException
        {
            this.service.stop();
        }
    }
}
