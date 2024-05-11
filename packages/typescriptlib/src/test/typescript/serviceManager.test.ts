import {ServiceConfiguration} from "@src/typescript/serviceConfiguration";
import {ServiceManager} from "@src/typescript/serviceManager"
import {ServiceManagerStrategy} from "@src/typescript/serviceManagerStrategy";
import {beforeAll, expect, test} from "vitest"
import {mock} from "vitest-mock-extended";

let mockStrategy: ServiceManagerStrategy;

beforeAll(() => { mockStrategy = mock<ServiceManagerStrategy>(); })

test("setDefaultStrategy/getService", () => {
    ServiceManager.setDefaultStrategy(mockStrategy);
    ServiceManager.getService("someService");
    expect(mockStrategy.getService).toBeCalledWith("someService", undefined);

    // Test with a config
    let serviceConfig: ServiceConfiguration = {foo : "bar"};
    ServiceManager.getService("someOtherService", serviceConfig);
    expect(mockStrategy.getService).toBeCalledWith("someOtherService", serviceConfig);
})

test("setDefaultStrategy/isServiceDefined", () => {
    ServiceManager.setDefaultStrategy(mockStrategy);
    ServiceManager.isServiceDefined("someService");
    expect(mockStrategy.isServiceDefined).toBeCalledWith("someService");
})

test("shutdown", () => {
    ServiceManager.setDefaultStrategy(mockStrategy);
    ServiceManager.shutdown();
    expect(mockStrategy.shutdown).toBeCalled();
})
