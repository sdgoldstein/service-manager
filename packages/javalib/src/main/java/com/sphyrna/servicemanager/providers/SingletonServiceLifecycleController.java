package com.sphyrna.servicemanager.providers;

import com.sphyrna.servicemanager.Service;
import com.sphyrna.servicemanager.ServiceConfiguration;
import com.sphyrna.servicemanager.ServiceException;
import com.sphyrna.servicemanager.ServiceInstanceProvider;
import com.sphyrna.servicemanager.ServiceLifecycleController;

public class SingletonServiceLifecycleController<S extends Service<C>, C extends ServiceConfiguration>
    implements ServiceLifecycleController<S, C>
{
    private ServiceInstanceProvider<S> serviceInstanceProvider;
    private C serviceConfiguration;
    private S singletonInstance;

    @Override
    public void init(ServiceInstanceProvider<S> instanceProvider, C configuration) throws ServiceException
    {
        this.serviceInstanceProvider = instanceProvider;
        this.serviceConfiguration = configuration;
    }

    @Override
    public S getService() throws ServiceException
    {
        // FIXME - Not Thread Safe
        if (this.singletonInstance == null)
        {
            this.singletonInstance = this.serviceInstanceProvider.createServiceInstance();
            this.singletonInstance.init(this.serviceConfiguration);
            this.singletonInstance.start();
        }

        return this.singletonInstance;
    }

    @Override
    public void shutdown()
    {
        // FIXME - Is there a bettter option than setting instance variables to null?

        if (this.singletonInstance != null)
        {
            this.singletonInstance.stop();
            this.singletonInstance.destroy();
            this.singletonInstance = null;
        }

        this.serviceConfiguration = null;
        this.serviceInstanceProvider = null;
    }
}
