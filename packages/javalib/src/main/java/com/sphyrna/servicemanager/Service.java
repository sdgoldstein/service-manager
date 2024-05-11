package com.sphyrna.servicemanager;

/**
 * A Service is a predefined component of functionality with a lifecycle
 * managed by the ServiceManger framework.
 */
public interface Service<C extends ServiceConfiguration>
{
    /**
     * Initializes the service instance
     *
     * @param configuration Service level configuration
     */
    void init(C configuration);

    /**
     * Starts the service instances
     */
    void start();

    /**
     * Stops the service instance
     */
    void stop();

    /**
     * Destroys the service instance.  Can be used to clean up memory or other
     * resources
     */
    void destroy();
}