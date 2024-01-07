import {GuardedMap} from '@sphyrna/tscore';

/**
 * A type defintion representing the value of a config parameter
 */
type ConfigValue = number|string|boolean|number[]|boolean[]|string[];

/**
 * A configuration for a service.  Provides a named set of properties
 */
interface ServiceConfiguration
{
    propertyMap: GuardedMap<string, ConfigValue>;
}

/**
 * A default service configuration implementation that accepts a map of
 * properties in its constructor
 */
class DefaultServiceConfigurationImpl implements ServiceConfiguration
{
    propertyMap: GuardedMap<string, ConfigValue>;

    constructor(properties: GuardedMap<string, any>)
    {
        this.propertyMap = properties;
    }
}

/**
 * An empty service configuration available for convenience
 */
const EMPTY_SERVICE_CONFIGURATION: ServiceConfiguration = new DefaultServiceConfigurationImpl(new Map<string, any>());

export {DefaultServiceConfigurationImpl, EMPTY_SERVICE_CONFIGURATION};
export type {ServiceConfiguration};
