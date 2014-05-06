/**
 * 
 */
package com.lifeease.pim.service.simple;

import com.lifeease.pim.service.Configuration;
import com.lifeease.pim.service.Service;
import com.lifeease.pim.service.ServiceController;
import com.lifeease.pim.service.ServiceException;
import com.lifeease.pim.service.ServiceProvider;

/**
 * @author sgoldstein
 * 
 */
public abstract class SimpleServiceProvider implements ServiceProvider
{
    /*
     * (non-Javadoc)
     * 
     * @see com.lifeease.pim.service.ServiceProvider#createService()
     */
    @Override
    public ServiceController createService() throws ServiceException
    {
        SimpleService createdService = null;
        try
        {
            Class<? extends SimpleService> simpleServiceClass = getServiceClass();
            createdService = simpleServiceClass.newInstance();
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

        // TODO Auto-generated method stub
        return new SimpleServiceController(createdService);
    }

    protected abstract Class<? extends SimpleService> getServiceClass();

    private class SimpleServiceController implements ServiceController
    {
        private SimpleService service;

        /**
         * @param createdService
         */
        public SimpleServiceController(SimpleService service)
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
