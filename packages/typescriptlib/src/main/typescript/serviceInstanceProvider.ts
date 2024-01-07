import {Service} from './service';

/**
 * A ServiceInstanceProvider creates a single instance of a service.
 */
interface ServiceInstanceProvider<T extends Service>
{
    /**
     * Create a Service instance
     */
    createServiceInstance(): T;
}

/**
 * DefaultServiceInstanceProviderImpl is a service provider that simply invokes the provided service constructor to
 * create the service instance
 */
class DefaultServiceInstanceProviderImpl<T extends Service> implements ServiceInstanceProvider<T>
{
    private _serviceClass: new() => T

    constructor(serviceClass: new() => T)
    {
        this._serviceClass = serviceClass;
    }

    createServiceInstance(): T
    {
        return new this._serviceClass();
    }
}

export {DefaultServiceInstanceProviderImpl};
export type {ServiceInstanceProvider}