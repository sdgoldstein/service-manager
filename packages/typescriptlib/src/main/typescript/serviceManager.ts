import {Service} from "./service.js";
import {ServiceConfiguration} from "./serviceConfiguration.js";
import {ConfigServiceManagerStrategy, ServiceManagerStrategy} from "./serviceManagerStrategy.js";

/**
 * The ServiceManager is used to manage and retrieve services.  It's a proxy to
 * a ServiceManagerStrategy implementation, which is used to modify the
 * ServiceManager behavior.  The static proxy simplifies client code
 *
 * @author sgoldstein
 *
 */
class ServiceManager
{
    private static _serviceManagerStrategy: ServiceManagerStrategy = new ConfigServiceManagerStrategy();

    /**
     * Retrieve a service.
     *
     * An optional config may be supplied for service instance level
     * configuration.  This must be supported by the associated
     * ServiceLifecycleManager
     */
    static getService<T extends Service, C extends ServiceConfiguration = ServiceConfiguration>(name: string,
                                                                                                config?: C): T
    {
        return ServiceManager._serviceManagerStrategy.getService<T>(name, config);
    }

    /**
     * Determine if a service with the specified name has been defined
     */
    static isServiceDefined(name: string): boolean
    {
        return ServiceManager._serviceManagerStrategy.isServiceDefined(name);
    }

    /**
     * Shutdown the ServiceManager and all associated services
     */
    static shutdown(): void
    {
        ServiceManager._serviceManagerStrategy.shutdown();
    }

    /**
     * Set the service manager strategy.  This strategy will be used to retrieve services based on the algorithm defined
     * in the ServiceManagerFactory implementnatation
     *
     * In the future, a ServiceManager may be able to use multiple strategies concurrently.  As of the current API, it
     * only supports a single strategy
     *
     * @param strategyToSet the service manager strategy to set
     */
    static setDefaultStrategy(strategyToSet: ServiceManagerStrategy)
    {
        ServiceManager._serviceManagerStrategy = strategyToSet;
    }
}

export {ServiceManager};
