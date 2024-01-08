import {Service} from "@src/typescript/service";
import {
    DefaultServiceConfigurationImpl,
    EMPTY_SERVICE_CONFIGURATION,
    ServiceConfiguration
} from "@src/typescript/serviceConfiguration";
import {ServiceError} from "@src/typescript/serviceError";
import {
    ServiceLifecycleController,
} from "@src/typescript/serviceLifecycleController";
import {DefaultServiceManagerStrategyImpl} from "@src/typescript/serviceManagerStrategy";
import {beforeAll, beforeEach, describe, expect, test} from "vitest"
import {mock, MockProxy, mockReset} from "vitest-mock-extended";

import {MockService, MockServiceInstanceProvider, MockServiceLifecycleController} from "./serviceFrameworkTestUtils";

let mockService: MockProxy<Service>;
let mockServiceInstanceProvider: MockServiceInstanceProvider;
let testServiceConfiguration: ServiceConfiguration;
let mockServiceLifecycleController: ServiceLifecycleController<MockProxy<Service>>;

beforeAll(() => {
    mockService = mock<Service>();
    mockServiceInstanceProvider = new MockServiceInstanceProvider(mockService);

    const properties = new Map<string, any>();
    properties.set("key1", "value1");
    properties.set("key2", "value2");

    testServiceConfiguration = new DefaultServiceConfigurationImpl(properties);
    mockServiceLifecycleController = mock<ServiceLifecycleController<MockProxy<Service>>>();
});

describe("DefaultServiceManagerStrategyImpl", () => {
    let strategyToTest: DefaultServiceManagerStrategyImpl;

    beforeAll(() => {

              });

    beforeEach(() => { strategyToTest = new DefaultServiceManagerStrategyImpl(); });

    test("registerService/isServiceDefined/getService", () => {
        const serviceNameOne = "serviceNameOne";
        expect(strategyToTest.isServiceDefined(serviceNameOne)).toStrictEqual(false);
        expect(() => strategyToTest.getService(serviceNameOne)).toThrowError(ServiceError);

        // register a service
        strategyToTest.registerService(serviceNameOne, mockServiceInstanceProvider, mockServiceLifecycleController);
        expect(strategyToTest.isServiceDefined(serviceNameOne)).toStrictEqual(true);
        strategyToTest.getService(serviceNameOne);
        expect(mockServiceLifecycleController.init)
            .toHaveBeenCalledWith(mockServiceInstanceProvider, EMPTY_SERVICE_CONFIGURATION);
        expect(mockServiceLifecycleController.getService).toHaveBeenCalled();

        // register a service with config
        const serviceNameTwo = "serviceNameTwo";
        strategyToTest.registerService(serviceNameTwo, mockServiceInstanceProvider, mockServiceLifecycleController,
                                       testServiceConfiguration);
        strategyToTest.getService(serviceNameTwo);
        expect(mockServiceLifecycleController.init)
            .toHaveBeenCalledWith(mockServiceInstanceProvider, testServiceConfiguration);

        // Override a service
        mockReset(mockServiceLifecycleController);
        strategyToTest.registerService(serviceNameOne, mockServiceInstanceProvider, mockServiceLifecycleController,
                                       testServiceConfiguration, true);
        expect(strategyToTest.isServiceDefined(serviceNameOne)).toStrictEqual(true);
        strategyToTest.getService(serviceNameOne);
        expect(mockServiceLifecycleController.init)
            .toHaveBeenCalledWith(mockServiceInstanceProvider, testServiceConfiguration);
        expect(mockServiceLifecycleController.getService).toHaveBeenCalled();

        // test overriding with registering with service with controller only
        mockReset(mockServiceLifecycleController);
        strategyToTest.registerServiceByControllerOnly(serviceNameTwo, mockServiceLifecycleController, true);
        expect(strategyToTest.isServiceDefined(serviceNameTwo)).toStrictEqual(true);
        strategyToTest.getService(serviceNameTwo);
        expect(mockServiceLifecycleController.init)
            .not.toHaveBeenCalledWith(mockServiceInstanceProvider, testServiceConfiguration);
        expect(mockServiceLifecycleController.getService).toHaveBeenCalled();

        // test service error for false override
        expect(() => strategyToTest.registerService(serviceNameOne, mockServiceInstanceProvider,
                                                    mockServiceLifecycleController, testServiceConfiguration, false))
            .toThrowError(ServiceError)
        expect(() => strategyToTest.registerService(serviceNameOne, mockServiceInstanceProvider,
                                                    mockServiceLifecycleController, testServiceConfiguration))
            .toThrowError(ServiceError)
        expect(() => strategyToTest.registerService(serviceNameTwo, mockServiceInstanceProvider,
                                                    mockServiceLifecycleController, testServiceConfiguration))
            .toThrowError(ServiceError)
    });

    test("registerServiceByControllerOnly/isServiceDefined/getService", () => {
        const serviceNameOne = "serviceNameOne";
        expect(strategyToTest.isServiceDefined(serviceNameOne)).toStrictEqual(false);
        expect(() => strategyToTest.getService(serviceNameOne)).toThrowError(ServiceError);

        // register a service
        strategyToTest.registerServiceByControllerOnly(serviceNameOne, mockServiceLifecycleController);
        expect(strategyToTest.isServiceDefined(serviceNameOne)).toStrictEqual(true);
        strategyToTest.getService(serviceNameOne);
        expect(mockServiceLifecycleController.init)
            .not.toHaveBeenCalledWith(mockServiceInstanceProvider, EMPTY_SERVICE_CONFIGURATION);
        expect(mockServiceLifecycleController.getService).toHaveBeenCalled();

        // Override a service
        mockReset(mockServiceLifecycleController);
        strategyToTest.registerServiceByControllerOnly(serviceNameOne, mockServiceLifecycleController, true);
        expect(strategyToTest.isServiceDefined(serviceNameOne)).toStrictEqual(true);
        strategyToTest.getService(serviceNameOne);
        expect(mockServiceLifecycleController.init)
            .not.toHaveBeenCalledWith(mockServiceInstanceProvider, testServiceConfiguration);
        expect(mockServiceLifecycleController.getService).toHaveBeenCalled();

        // test overriding with registering with service with provider and config
        mockReset(mockServiceLifecycleController);
        const serviceNameTwo = "serviceNameTwo";
        strategyToTest.registerServiceByControllerOnly(serviceNameTwo, mockServiceLifecycleController);
        expect(strategyToTest.isServiceDefined(serviceNameTwo)).toStrictEqual(true);
        strategyToTest.registerService(serviceNameTwo, mockServiceInstanceProvider, mockServiceLifecycleController,
                                       testServiceConfiguration, true);
        expect(strategyToTest.isServiceDefined(serviceNameTwo)).toStrictEqual(true);
        strategyToTest.getService(serviceNameTwo);
        expect(mockServiceLifecycleController.init)
            .toHaveBeenCalledWith(mockServiceInstanceProvider, testServiceConfiguration);
        expect(mockServiceLifecycleController.getService).toHaveBeenCalled();

        // test service error for false override
        expect(
            () => strategyToTest.registerServiceByControllerOnly(serviceNameOne, mockServiceLifecycleController, false))
            .toThrowError(ServiceError)
        expect(() => strategyToTest.registerServiceByControllerOnly(serviceNameOne, mockServiceLifecycleController))
            .toThrowError(ServiceError)
        expect(() => strategyToTest.registerServiceByControllerOnly(serviceNameTwo, mockServiceLifecycleController))
            .toThrowError(ServiceError)
    });

    test("registerServiceByClass/isServiceDefined/getService", () => {
        const serviceNameOne = "serviceNameOne";
        expect(strategyToTest.isServiceDefined(serviceNameOne)).toStrictEqual(false);
        expect(() => strategyToTest.getService(serviceNameOne)).toThrowError(ServiceError);

        // register a service
        strategyToTest.registerServiceByClass(serviceNameOne, MockService, MockServiceLifecycleController);
        expect(strategyToTest.isServiceDefined(serviceNameOne)).toStrictEqual(true);
        strategyToTest.getService(serviceNameOne);

        // register a service with config
        const serviceNameTwo = "serviceNameTwo";
        strategyToTest.registerServiceByClass(serviceNameTwo, MockService, MockServiceLifecycleController,
                                              testServiceConfiguration);
        expect(strategyToTest.isServiceDefined(serviceNameOne)).toStrictEqual(true);
        strategyToTest.getService(serviceNameTwo);

        // Override a service
        mockReset(mockServiceLifecycleController);
        strategyToTest.registerServiceByClass(serviceNameOne, MockService, MockServiceLifecycleController,
                                              testServiceConfiguration, true);
        expect(strategyToTest.isServiceDefined(serviceNameOne)).toStrictEqual(true);
        strategyToTest.getService(serviceNameOne);

        // test overriding with registering with service with controller only
        mockReset(mockServiceLifecycleController);
        strategyToTest.registerServiceByControllerOnly(serviceNameTwo, mockServiceLifecycleController, true);
        expect(strategyToTest.isServiceDefined(serviceNameTwo)).toStrictEqual(true);
        strategyToTest.getService(serviceNameTwo);
        expect(mockServiceLifecycleController.init)
            .not.toHaveBeenCalledWith(mockServiceInstanceProvider, testServiceConfiguration);
        expect(mockServiceLifecycleController.getService).toHaveBeenCalled();

        // test service error for false override
        expect(() => strategyToTest.registerServiceByClass(serviceNameOne, MockService, MockServiceLifecycleController,
                                                           testServiceConfiguration, false))
            .toThrowError(ServiceError);
        expect(() => strategyToTest.registerServiceByClass(serviceNameOne, MockService, MockServiceLifecycleController,
                                                           testServiceConfiguration))
            .toThrowError(ServiceError);
        expect(() => strategyToTest.registerServiceByClass(serviceNameTwo, MockService, MockServiceLifecycleController,
                                                           testServiceConfiguration))
            .toThrowError(ServiceError);
    });

    test("registerSingletonService/isServiceDefined/getService", () => {
        const serviceNameOne = "serviceNameOne";
        expect(strategyToTest.isServiceDefined(serviceNameOne)).toStrictEqual(false);
        expect(() => strategyToTest.getService(serviceNameOne)).toThrowError(ServiceError);

        // register a service
        strategyToTest.registerSingletonService(serviceNameOne, MockService);
        expect(strategyToTest.isServiceDefined(serviceNameOne)).toStrictEqual(true);
        let serviceOne = strategyToTest.getService(serviceNameOne);
        expect(serviceOne).toBeInstanceOf(MockService);

        // Verify singleton behavior
        let serviceOneAgain = strategyToTest.getService(serviceNameOne);
        expect(serviceOneAgain).toStrictEqual(serviceOne)

        // register a service with config
        const serviceNameTwo = "serviceNameTwo";
        strategyToTest.registerSingletonService(serviceNameTwo, MockService, testServiceConfiguration);
        expect(strategyToTest.isServiceDefined(serviceNameOne)).toStrictEqual(true);
        let serviceTwo = strategyToTest.getService(serviceNameTwo);
        expect(serviceTwo).toBeInstanceOf(MockService);

        // Override a service
        mockReset(mockServiceLifecycleController);
        strategyToTest.registerSingletonService(serviceNameOne, MockService, testServiceConfiguration, true);
        expect(strategyToTest.isServiceDefined(serviceNameOne)).toStrictEqual(true);
        serviceOne = strategyToTest.getService(serviceNameOne);
        expect(serviceOne).toBeInstanceOf(MockService);

        // test overriding with registering with service with controller only
        mockReset(mockServiceLifecycleController);
        strategyToTest.registerServiceByControllerOnly(serviceNameTwo, mockServiceLifecycleController, true);
        expect(strategyToTest.isServiceDefined(serviceNameTwo)).toStrictEqual(true);
        strategyToTest.getService(serviceNameTwo);
        expect(mockServiceLifecycleController.init)
            .not.toHaveBeenCalledWith(mockServiceInstanceProvider, testServiceConfiguration);
        expect(mockServiceLifecycleController.getService).toHaveBeenCalled();

        // test service error for false override
        expect(
            () => strategyToTest.registerSingletonService(serviceNameOne, MockService, testServiceConfiguration, false))
            .toThrowError(ServiceError);
        expect(() => strategyToTest.registerSingletonService(serviceNameOne, MockService, testServiceConfiguration))
            .toThrowError(ServiceError);
        expect(() => strategyToTest.registerSingletonService(serviceNameTwo, MockService, testServiceConfiguration))
            .toThrowError(ServiceError);
    });

    test("shutdown", () => {
        const serviceNameOne = "serviceNameOne";

        // register a service
        strategyToTest.registerService(serviceNameOne, mockServiceInstanceProvider, mockServiceLifecycleController);
        expect(strategyToTest.isServiceDefined(serviceNameOne)).toStrictEqual(true);
        strategyToTest.getService(serviceNameOne);

        strategyToTest.shutdown();
        expect(mockServiceLifecycleController.shutdown).toHaveBeenCalled();

        // Once it's shutdown, expect methods to throw errors
        expect(() => strategyToTest.getService(serviceNameOne)).toThrowError(ServiceError);
    });
});
