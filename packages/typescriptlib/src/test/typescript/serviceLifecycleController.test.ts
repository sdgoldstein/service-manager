import {Service} from "@src/typescript/service";
import {DefaultServiceConfigurationImpl, ServiceConfiguration} from "@src/typescript/serviceConfiguration";
import {ServiceError} from "@src/typescript/serviceError";
import {ServiceInstanceProvider} from "@src/typescript/serviceInstanceProvider";
import {SingletonServiceLifecycleControllerImpl} from "@src/typescript/serviceLifecycleController";
import {beforeAll, beforeEach, describe, expect, test} from "vitest";
import {mock, MockProxy, mockReset} from "vitest-mock-extended";

describe("Service Lifecycle Controller", () => {
    describe("SingletonServiceLifecycleControllerImpl", () => {
        let mockService: MockProxy<Service>;
        class MockServiceInstanceProvider implements ServiceInstanceProvider<MockProxy<Service>>
        {
            createdTimes: number = 0;

            createServiceInstance(): MockProxy<Service>
            {
                this.createdTimes++;
                return mockService;
            }
        };
        let mockServiceInstanceProvider: MockServiceInstanceProvider;
        let testServiceConfiguration: ServiceConfiguration;
        let controllerToTest: SingletonServiceLifecycleControllerImpl<MockProxy<Service>>;

        beforeAll(() => {
            mockService = mock<Service>();
            const properties = new Map<string, any>();
            properties.set("key1", "value1");
            properties.set("key2", "value2");

            testServiceConfiguration = new DefaultServiceConfigurationImpl(properties);
        });

        beforeEach(() => {
            mockReset(mockService);

            mockServiceInstanceProvider = new MockServiceInstanceProvider();
            controllerToTest = new SingletonServiceLifecycleControllerImpl();
        });

        test("SingletonServiceLifecycleControllerImpl test initialize and start", () => {
            // First call getService without calling init
            expect(() => controllerToTest.getService()).toThrowError(ServiceError);

            // Now, initialize and get service
            controllerToTest.init(mockServiceInstanceProvider, testServiceConfiguration);
            const instance = controllerToTest.getService();
            expect(instance).toStrictEqual(mockService);
            expect(mockService.init).toHaveBeenCalledWith(testServiceConfiguration);
            expect(mockService.start).toHaveBeenCalled();
            expect(mockServiceInstanceProvider.createdTimes).toStrictEqual(1);

            // Try to obtain it again
            mockReset(mockService);
            const sameInstance = controllerToTest.getService();
            expect(sameInstance).toStrictEqual(mockService);
            expect(mockService.init).not.toHaveBeenCalled();
            expect(mockService.start).not.toHaveBeenCalled();
            expect(mockServiceInstanceProvider.createdTimes).toStrictEqual(1); // Should still be one
        });

        test("SingletonServiceLifecycleControllerImpl test shutdown, stop, destroy", () => {
            // Initialize and get the service started
            controllerToTest.init(mockServiceInstanceProvider, testServiceConfiguration);
            controllerToTest.getService();

            // Now, shutdown
            controllerToTest.shutdown();
            expect(mockService.stop).toHaveBeenCalled();
            expect(mockService.destroy).toHaveBeenCalled();

            // Once it's shutdown, expect methods to throw errors
            expect(() => controllerToTest.init(mockServiceInstanceProvider, testServiceConfiguration))
                .toThrowError(ServiceError);
            expect(() => controllerToTest.getService()).toThrowError(ServiceError);
            expect(() => controllerToTest.shutdown()).toThrowError(ServiceError);
        });
    });
});
