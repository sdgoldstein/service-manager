
/**
 * A type definition representing the value of a config parameters.  Restricting for now.  This may open up in the
 * future
 */
type ConfigValue = number|string|boolean|number[]|boolean[]|string[]|JSON;
type OnlyConfigParams = {
    readonly [key: string]: ConfigValue;
}
/**
 * A configuration for a service.  This interface reserves the ability for the framework to add configuration values for
 * all services in the future
 */
interface ServiceConfiguration extends OnlyConfigParams {
}

/**
 * An empty service configuration available for convenience
 */
const EMPTY_SERVICE_CONFIGURATION: ServiceConfiguration = {};

export {EMPTY_SERVICE_CONFIGURATION};
export type {ServiceConfiguration};
