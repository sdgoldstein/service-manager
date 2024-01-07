import {ServiceConfiguration} from './serviceConfiguration';

/**
 * A Service is a predefined component of functionality with a lifecycle
 * managed by the ServiceManger.
 *
 * @author sgoldstein
 */
interface Service
{
    /**
     * Initializes the service instance
     *
     * @param configuration Service level configuration
     */
    init(configuration: ServiceConfiguration): void;

    /**
     * Starts the service instances
     */
    start(): void;

    /**
     * Stops the service instance
     */
    stop(): void;

    /**
     * Destroys the service instance.  Can be used to clean up memory or other
     * resources
     */
    destroy(): void;
}

/**
 * Base instance of a Service which is a noop for all methods.  Extend to create
 * a new service and implement the methods that are required
 */
abstract class BaseService implements Service
{
    destroy(): void
    {
        // Do Nothing Implementation
    }

    init(configuration: ServiceConfiguration): void
    {
        // Do Nothing Implementation
    }

    start(): void
    {
        // Do Nothing Implementation
    }

    stop(): void
    {
        // Do Nothing Implementation
    }
}

export {BaseService};
export type {Service};
