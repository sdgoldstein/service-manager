package com.sphyrna.servicemanager.defaultimpl;

import com.sphyrna.servicemanager.Service;
import com.sphyrna.servicemanager.ServiceException;
import com.sphyrna.servicemanager.ServiceInstanceProvider;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

public class ServiceInstanceProviderImpl<S extends Service> implements ServiceInstanceProvider<S>
{

    private Class<S> serviceClass;

    public ServiceInstanceProviderImpl(Class<S> serviceClass)
    {
        this.serviceClass = Objects.requireNonNull(serviceClass, "serviceClass cannot be null");
    }

    @Override
    public S createServiceInstance() throws ServiceException
    {
        S createdService = null;

        try
        {
            createdService = this.serviceClass.getDeclaredConstructor().newInstance();
        }
        catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException |
               NoSuchMethodException | SecurityException exception)
        {
            throw new ServiceException("Failed to instantiate service", exception);
        }

        return createdService;
    }
}
