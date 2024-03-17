import {GuardedMap} from "@sphyrna/tscore";

import {Service} from "./service.js";
import {EMPTY_SERVICE_CONFIGURATION, ServiceConfiguration} from "./serviceConfiguration.js";
import {ServiceError} from "./serviceError.js";
import {DefaultServiceInstanceProviderImpl, ServiceInstanceProvider} from "./serviceInstanceProvider.js";
import {KnownServiceLifecycleControllers, ServiceLifecycleController} from "./serviceLifecycleController.js";

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

    private _serviceInstanceProvider: ServiceInstanceProvider<T>|undefined;

    serviceConfiguration: ServiceConfiguration;

    constructor(serviceLifecycleController: ServiceLifecycleController<T>);
    constructor(serviceLifecycleController: ServiceLifecycleController<T>,
                serviceInstanceProvider: ServiceInstanceProvider<T>, config?: ServiceConfiguration);
    constructor(readonly serviceLifecycleController: ServiceLifecycleController<T>,
                serviceInstanceProvider?: ServiceInstanceProvider<T>, config?: ServiceConfiguration)
    {
        this._serviceInstanceProvider = serviceInstanceProvider;

        this.serviceConfiguration = config ?? EMPTY_SERVICE_CONFIGURATION;
    }

    hasServiceInstanceProvider(): boolean
    {
        return (this._serviceInstanceProvider !== undefined);
    }

    get serviceInstanceProvider(): ServiceInstanceProvider<T>
    {
        if (this._serviceInstanceProvider === undefined)
        {
            throw new ServiceError("get serviceInstanceProvider called when one doesn't exist")
        }

        return this._serviceInstanceProvider;
    }
}

class DefaultServiceManagerStrategyImpl implements ServiceManagerStrategy
{
    private readonly CONTROLLERS_FOR_ACTIVE_SERVICES: GuardedMap<string, ServiceLifecycleController<Service>> =
        new Map<string, ServiceLifecycleController<Service>>();

    private readonly SERVICE_DEFINITIONS: GuardedMap<string, ServiceDefinition<Service>> =
        new Map<string, ServiceDefinition<Service>>();

    /**
     * Register a service.  This is the minimal number of inputs needed to register a new service.
     *
     * IMPORTANT NOTE: If registering a lifecycle controller through this method, the init(serviceInstanceProvider:
     * ServiceInstanceProvider<T>, config: C) method will NOT be called.  If it must be called, it should be invoked by
     * the client before registering the service
     *
     * @param name
     *            the name of the defined service
     * @param serviceLifecycleController
     *            controls the service lifeycle. For known controllers,
     * see {@KnownServiceLifecycleControllers}
     * @param override
     *            An optional boolean indicating whether or not to override an
     * existing defintion with the same name. If this is false and a service
     * definition already exists, an error will be thrown
     */
    registerServiceByControllerOnly<T extends Service, C extends ServiceConfiguration = ServiceConfiguration>(
        name: string, serviceLifecycleController: ServiceLifecycleController<T, C>, override?: boolean): void
    {
        if (this.SERVICE_DEFINITIONS.has(name))
        {
            this.handleServiceOverride(name, override);
        }

        const serviceDefinition: ServiceDefinition<T> = new ServiceDefinition(serviceLifecycleController);
        this.SERVICE_DEFINITIONS.set(name, serviceDefinition);
    }

    registerService<T extends Service, C extends ServiceConfiguration = ServiceConfiguration>(
        name: string, serviceInstanceProvider: ServiceInstanceProvider<T>,
        serviceLifecycleController: ServiceLifecycleController<T, C>, config?: C, override?: boolean): void
    {
        if (this.SERVICE_DEFINITIONS.has(name))
        {
            this.handleServiceOverride(name, override);
        }

        const serviceDefinition: ServiceDefinition<T> =
            new ServiceDefinition(serviceLifecycleController, serviceInstanceProvider, config);
        this.SERVICE_DEFINITIONS.set(name, serviceDefinition);
    }

    /**
     * Register a service providing a contructor method for a service class and service lifecycle controller class
     *
     * @param name
     *            the name of the defined service
     * @param serviceClass
     *            the class that implements the service
     * @param serviceLifecycleControllerClass
     *            the class that controls the service lifeycle. For known controllers,
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
        name: string, serviceClass: new() => T, serviceLifecycleControllerClass: new() => ServiceLifecycleController<T>,
        config?: C, override?: boolean): void
    {
        const serviceProvider = new DefaultServiceInstanceProviderImpl(serviceClass);
        const serviceLifecycleController = new serviceLifecycleControllerClass();
        this.registerService(name, serviceProvider, serviceLifecycleController, config, override);
    }

    /**
     * Register a service that has a singleton lifecycle (only one instance exists)
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
        this.registerServiceByClass(name, serviceClass, KnownServiceLifecycleControllers.SINGLETON, config, override);
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

            if (serviceDefinition.hasServiceInstanceProvider())
            {
                serviceLifecycleController.init(serviceDefinition.serviceInstanceProvider,
                                                serviceDefinition.serviceConfiguration);
            }
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

    private handleServiceOverride(name: string, override?: boolean)
    {
        if (override)
        {
            // This is an override.  Remove defintions.  Shutdown lifecycle controller
            this.SERVICE_DEFINITIONS.delete(name);
            if (this.CONTROLLERS_FOR_ACTIVE_SERVICES.has(name))
            {
                const controllerToRemove = this.CONTROLLERS_FOR_ACTIVE_SERVICES.get(name);
                controllerToRemove.shutdown();
                this.CONTROLLERS_FOR_ACTIVE_SERVICES.delete(name);
            }
        }
        else
        {
            throw new ServiceError(
                `Service with name, ${name}, is already defined.  Specify override=true if it should be replaced`);
        }
    }
}

/**
 * A strategy that reads service configuration from serviceConfig.json.  FIXME - To Be Implemented
 */
class ConfigServiceManagerStrategy extends DefaultServiceManagerStrategyImpl implements ServiceManagerStrategy
{
}

export type {ServiceManagerStrategy};
export {ConfigServiceManagerStrategy, DefaultServiceManagerStrategyImpl};
