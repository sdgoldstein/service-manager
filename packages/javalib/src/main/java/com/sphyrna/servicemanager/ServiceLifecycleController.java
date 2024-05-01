package com.sphyrna.servicemanager;

/**
 * ServiceController is responsible for provide a control proxy to the Service.
 * It's invoked by the ServiceManager to create, initialize, start, stop, and
 * destroy a Service
 *
 */
public interface ServiceLifecycleController<S extends Service<C>, C extends ServiceConfiguration>
{
    /**
     * Initialize the service.
     *
     * @throws ServiceException
     *             if initialization fails
     */
    public void init(ServiceInstanceProvider<S> instanceProvider, C configuration) throws ServiceException;

    /**
     * Retrieve the wrapped Service
     *
     * @return the wrapped Service
     * @throws ServiceException
     */
    public S getService() throws ServiceException;

    /**
     * Shutdown the service lifecycle and clean up
     */
    void shutdown();
}
