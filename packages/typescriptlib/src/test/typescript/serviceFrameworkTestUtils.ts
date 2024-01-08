import {BaseService, Service} from "@src/typescript/service";
import {ServiceConfiguration} from "@src/typescript/serviceConfiguration";
import {ServiceInstanceProvider} from "@src/typescript/serviceInstanceProvider";
import {ServiceLifecycleController} from "@src/typescript/serviceLifecycleController";
import {mock, MockProxy} from "vitest-mock-extended";

class MockServiceInstanceProvider implements ServiceInstanceProvider<MockProxy<Service>>
{
    createdTimes: number = 0;

    constructor(public mockService: MockProxy<Service>) {}

    createServiceInstance(): MockProxy<Service>
    {
        this.createdTimes++;
        return this.mockService;
    }
}

class MockService extends BaseService
{
};

class MockServiceLifecycleController<T extends Service> implements ServiceLifecycleController<T>
{
    constructor()
    {
        return mock<ServiceLifecycleController<T>>();
    }

    init(serviceInstanceProvider: ServiceInstanceProvider<T>, config: ServiceConfiguration): void
    {
        throw new Error("Method not implemented.");
    }
    shutdown(): void
    {
        throw new Error("Method not implemented.");
    }
    getService(): T
    {
        throw new Error("Method not implemented.");
    }
};

export {MockServiceLifecycleController, MockServiceInstanceProvider, MockService};