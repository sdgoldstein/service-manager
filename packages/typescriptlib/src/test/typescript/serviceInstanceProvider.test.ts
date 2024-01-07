import {BaseService} from "@src/typescript/service";
import {DefaultServiceInstanceProviderImpl} from "@src/typescript/serviceInstanceProvider";
import {describe, expect, test} from "vitest";

// As I don't know how to properly spy on a constructor, I'm creating a simple service that can indicate if the
// constructor was called
class TestService extends BaseService
{
    wasCalled: boolean = false;
    constructor()
    {
        super();
        this.wasCalled = true;
    }
};

describe("Service Instance Provider", () => {
    test("DefaultServiceInstanceProviderImpl should create a new instance of the service", () => {
        const provider = new DefaultServiceInstanceProviderImpl(TestService);
        const instance = provider.createServiceInstance();

        expect(instance.wasCalled).toBe(true);
        expect(instance instanceof TestService).toBe(true);
    });
});
