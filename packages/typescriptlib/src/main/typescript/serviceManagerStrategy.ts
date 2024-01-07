import {GuardedMap} from "@sphyrna/tscore";

import {Service} from "./service";
import {EMPTY_SERVICE_CONFIGURATION, ServiceConfiguration} from "./serviceConfiguration";
import {DefaultServiceInstanceProviderImpl, ServiceInstanceProvider} from "./serviceInstanceProvider";
import {KnownServiceLifecycleControllers, ServiceLifecycleController} from "./serviceLifecycleController";
import {ServiceError} from "./serviceError";

/**
 * Defines a Service Manager Strategy that controls the behavior of the ServiceManager
 */
interface ServiceManagerStrategy
{
    /**
     * Retrieve a service.
     *
     * An Optional config may be supplied for service instance level
     * configuration.  This must be supported by the associated
     * ServiceLifecycleManager
     */
    getService<T extends Service, C extends ServiceConfiguration = ServiceConfiguration>(name: string, config?: C): T;

    /**
     * Determine if a service with the specified name has been defined
     */
    isServiceDefined(name: string): boolean;

    /**
     * Shutdown the ServiceManager and all associated services
     */
    shutdown(): void;
}

/**
 * Internal class
 */
class ServiceDefinition<T extends Service>
{
    serviceConfiguration: ServiceConfiguration;

    constructor(readonly serviceInstanceProvider: ServiceInstanceProvider<T>,
                readonly serviceLifecycleController: ServiceLifecycleController<T>, config?: ServiceConfiguration)
    {
        if (!config)
        {
            this.serviceConfiguration = EMPTY_SERVICE_CONFIGURATION;
        }
        else
        {
            this.serviceConfiguration = config;
        }
    }
}

abstract class BaseServiceManagerStrategy implements ServiceManagerStrategy
{
    private readonly CONTROLLERS_FOR_ACTIVE_SERVICES: GuardedMap<string, ServiceLifecycleController<Service>> =
        new Map<string, ServiceLifecycleController<Service>>();

    private readonly SERVICE_DEFINITIONS: GuardedMap<string, ServiceDefinition<Service>> =
        new Map<string, ServiceDefinition<Service>>();

    /**
     * Register a service
     *
     * @param name
     *            the name of the defined service
     * @param serviceInstanceProvider
     *            a factory that will create an instance of the service
     * @param serviceLifecycleController
     *            controls the service lifeycle. For known controllers,
     * see {@KnownServiceLifecycleControllers}
     * @param config
     *            An Optional config may be supplied for service definition level
     * configuration.
     * @param override
     *            An optional boolean indicating whether or not to override an
     * existing defintion with the same name. If this is false and a service
     * definition already exists, an error will be thrown
     */
    protected registerService<T extends Service, C extends ServiceConfiguration = ServiceConfiguration>(
        name: string, serviceInstanceProvider: ServiceInstanceProvider<T>,
        serviceLifecycleController: ServiceLifecycleController<T>, config?: C, override?: boolean): void
    {
        if (this.SERVICE_DEFINITIONS.has(name) && !override)
        {
            throw new ServiceError(`Service with name, ${name}, is already defined`);
        }

        const serviceDefinition: ServiceDefinition<T> =
            new ServiceDefinition(serviceInstanceProvider, serviceLifecycleController, config);
        this.SERVICE_DEFINITIONS.set(name, serviceDefinition);
    }

    getService<T extends Service, C extends ServiceConfiguration = ServiceConfiguration>(name: string, config?: C): T
    {
        let serviceLifecycleController: ServiceLifecycleController<Service>;

        if (!this.SERVICE_DEFINITIONS.has(name))
        {
            throw new ServiceError(`Service not defined: ${name}`);
        }

        // Check if the service lifecycle controller has already been initialized
        if (this.CONTROLLERS_FOR_ACTIVE_SERVICES.has(name))
        {
            serviceLifecycleController = this.CONTROLLERS_FOR_ACTIVE_SERVICES.get(name);
        }
        else
        {
            const serviceDefinition: ServiceDefinition<Service> = this.SERVICE_DEFINITIONS.get(name);
            serviceLifecycleController = serviceDefinition.serviceLifecycleController;

            serviceLifecycleController.init(serviceDefinition.serviceInstanceProvider,
                                            serviceDefinition.serviceConfiguration);
            this.CONTROLLERS_FOR_ACTIVE_SERVICES.set(name, serviceLifecycleController);
        }

        // FIXME - Is "as T" the best approach here?
        return serviceLifecycleController.getService() as T;
    }

    isServiceDefined(name: string): boolean
    {
        return this.SERVICE_DEFINITIONS.has(name);
    }

    shutdown(): void
    {
        this.CONTROLLERS_FOR_ACTIVE_SERVICES.forEach((nextServiceController: ServiceLifecycleController<Service>,
                                                      key: string) => { nextServiceController.shutdown(); });

        this.CONTROLLERS_FOR_ACTIVE_SERVICES.clear();
        this.SERVICE_DEFINITIONS.clear();
    }
}

/**
 * Concrete implementation that can be used at runtime to programmatically register services
 */
class RuntimeInitializedServiceManagerStrategy extends BaseServiceManagerStrategy implements ServiceManagerStrategy
{
    /**
     * Convenience method to register a service that has a singleton lifecycle (only one instance exists)
     *
     * @param name
     *            the name of the defined service
     * @param serviceClass
     *            the class that implements the service
     * @param config
     *            An Optional config may be supplied for service definition level
     * configuration.
     * @param override
     *            An optional boolean indicating whether or not to override an
     * existing defintion with the same name. If this is false and a service
     * definition already exists, an error will be thrown
     */
    registerSingletonService<T extends Service, C extends ServiceConfiguration = ServiceConfiguration>(
        name: string, serviceClass: new() => T, config?: C, override?: boolean): void
    {
        this.registerServiceByClass(name, serviceClass, new KnownServiceLifecycleControllers.SINGLETON(), config,
                                    override);
    }

    /**
     * Convenience method to register a service providing a contructor method for a class
     *
     * @param name
     *            the name of the defined service
     * @param serviceClass
     *            the class that implements the service
     * @param serviceLifecycleController
     *            controls the service lifeycle. For known controllers,
     * see {@KnownServiceLifecycleControllers}
     * @param config
     *            An Optional config may be supplied for service definition level
     * configuration.
     * @param override
     *            An optional boolean indicating whether or not to override an
     * existing defintion with the same name. If this is false and a service
     * definition already exists, an error will be thrown
     */
    registerServiceByClass<T extends Service, C extends ServiceConfiguration = ServiceConfiguration>(
        name: string, serviceClass: new() => T, serviceLifecycleController: ServiceLifecycleController<T>, config?: C,
        override?: boolean): void
    {
        const serviceProvider = new DefaultServiceInstanceProviderImpl(serviceClass);
        this.registerService(name, serviceProvider, serviceLifecycleController, config, override);
    }

    /**
     * Register a service
     *
     * @param name
     *            the name of the defined service
     * @param serviceInstanceProvider
     *            a factory that will create an instance of the service
     * @param serviceLifecycleController
     *            controls the service lifeycle. For known controllers,
     * see {@KnownServiceLifecycleControllers}
     * @param config
     *            An Optional config may be supplied for service definition level
     * configuration.
     * @param override
     *            An optional boolean indicating whether or not to override an
     * existing defintion with the same name. If this is false and a service
     * definition already exists, an error will be thrown
     */
    registerService<T extends Service, C extends ServiceConfiguration = ServiceConfiguration>(
        name: string, serviceInstanceProvider: ServiceInstanceProvider<T>,
        serviceLifecycleController: ServiceLifecycleController<T>, config?: C, override?: boolean): void
    {
        super.registerService(name, serviceInstanceProvider, serviceLifecycleController, config, override);
    }
}

/**
 * A strategy that reads service configuration from serviceConfig.json.  FIXME - To Be Implemented
 */
class ConfigServiceManagerStrategy extends BaseServiceManagerStrategy implements ServiceManagerStrategy
{
}

export type {ServiceManagerStrategy};
export {ConfigServiceManagerStrategy, RuntimeInitializedServiceManagerStrategy};
