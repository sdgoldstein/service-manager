import {Service} from "./service";
import {EMPTY_SERVICE_CONFIGURATION, ServiceConfiguration} from "./serviceConfiguration";
import {ServiceError} from "./serviceError";
import {ServiceInstanceProvider} from "./serviceInstanceProvider";

/**
 * ServiceLifecycleController is responsible for provide a control proxy to the Service.
 * It's invoked by the ServiceManager to create, initialize, start, stop, and
 * destroy a Service
 */
interface ServiceLifecycleController<T extends Service, C extends ServiceConfiguration = ServiceConfiguration>
{
    /**
     * Register a service with the lifecycle controller.
     * @param serviceInstanceProvider the factory that will create class instances of the service class
     * @param config service level configuration
     */
    init(serviceInstanceProvider: ServiceInstanceProvider<T>, config: C): void;

    /**
     * Shutdown the service lifecycle and clean up
     */
    shutdown(): void;

    /**
     * Retrieve a service instance
     *
     * @return a service instance
     */
    getService(): T;
}

/**
 * Lifecycle controller that creates singletons
 */
class SingletonServiceLifecycleControllerImpl<T extends Service, C extends ServiceConfiguration = ServiceConfiguration>
    implements ServiceLifecycleController<T, C>
{
    private _config: ServiceConfiguration = EMPTY_SERVICE_CONFIGURATION;
    private _serviceInstanceProvider?: ServiceInstanceProvider<T>;
    private _singletonInstance?: T;

    init(serviceInstanceProvider: ServiceInstanceProvider<T>, config: C): void
    {
        this._serviceInstanceProvider = serviceInstanceProvider;
        this._config = config;
    }

    getService(): T
    {
        if (this._singletonInstance === undefined)
        {
            if (this._serviceInstanceProvider === undefined)
            {
                throw new ServiceError("Unexpected state. ServiceInstanceProvider not set");
            }

            this._singletonInstance = this._serviceInstanceProvider.createServiceInstance();
            this._singletonInstance.init(this._config);
            this._singletonInstance.start();
        }

        return this._singletonInstance;
    }

    shutdown(): void
    {
        if (this._singletonInstance !== undefined)
        {
            this._singletonInstance.stop();
            this._singletonInstance.destroy();
            this._singletonInstance = undefined;
        }
        this._config = EMPTY_SERVICE_CONFIGURATION;
        this._serviceInstanceProvider = undefined;
    }
}

/**
 * Known ServiceLifecycleController implementations.  To create an instance, use the new keyword with the constant
 * member variable
 *
 * For example, "new KnownServiceLifecycleControllers.SINGLETON()"
 */
class KnownServiceLifecycleControllers
{
    public static readonly SINGLETON = SingletonServiceLifecycleControllerImpl;
}

export type {ServiceLifecycleController};
export {SingletonServiceLifecycleControllerImpl, KnownServiceLifecycleControllers}