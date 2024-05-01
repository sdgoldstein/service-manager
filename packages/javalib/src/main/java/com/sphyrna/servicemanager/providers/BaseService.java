
package com.sphyrna.servicemanager.providers;

import com.sphyrna.servicemanager.Service;
import com.sphyrna.servicemanager.ServiceConfiguration;
import java.util.logging.Logger;

/**
 * Base instance of a Service which is a noop for all methods.  Extend to create
 * a new service and implement the methods that are required
 */
public abstract class BaseService<T extends ServiceConfiguration> implements Service<T>
{

    @Override
    public void init(T configuration)
    {
    }

    @Override
    public void start()
    {
    }

    @Override
    public void stop()
    {
    }

    @Override
    public void destroy()
    {
    }

    protected Logger getLogger()
    {
        return Logger.getLogger(this.getClass().getName());
    }
}
