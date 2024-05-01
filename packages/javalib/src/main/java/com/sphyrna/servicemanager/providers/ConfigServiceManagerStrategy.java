package com.sphyrna.servicemanager.providers;

import com.sphyrna.servicemanager.Service;
import com.sphyrna.servicemanager.ServiceConfiguration;
import com.sphyrna.servicemanager.ServiceManagerStrategy;

public class ConfigServiceManagerStrategy<S extends Service<C>, C extends ServiceConfiguration>
    implements ServiceManagerStrategy<S, C>
{

    @Override
    public S getService(String name)
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getService'");
    }

    @Override
    public S getService(String name, ServiceConfiguration serviceConfiguration)
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getService'");
    }

    @Override
    public boolean isServiceDefined(String name)
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isServiceDefined'");
    }

    @Override
    public void shutdown()
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'shutdown'");
    }
}
