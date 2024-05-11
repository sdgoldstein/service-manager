package com.sphyrna.servicemanager.providers;

import com.sphyrna.servicemanager.Service;
import com.sphyrna.servicemanager.ServiceConfiguration;
import com.sphyrna.servicemanager.ServiceManagerStrategy;

public class ConfigServiceManagerStrategy implements ServiceManagerStrategy
{

    @Override
    public <S extends Service> S getService(String name)
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getService'");
    }

    @Override
    public <S extends Service<C>, C extends ServiceConfiguration> S getService(String name, C serviceConfiguration)
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
