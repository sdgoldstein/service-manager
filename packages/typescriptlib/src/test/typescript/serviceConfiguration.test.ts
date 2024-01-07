import {DefaultServiceConfigurationImpl, EMPTY_SERVICE_CONFIGURATION} from "@src/typescript/serviceConfiguration";
import {describe, expect, test} from "vitest";

describe("Service Configuration", () => {
    test("DefaultServiceConfigurationImpl should initialize correctly", () => {
        const properties = new Map<string, any>();
        properties.set("key1", "value1");
        properties.set("key2", "value2");

        const config = new DefaultServiceConfigurationImpl(properties);

        expect(config.propertyMap.size).toBe(2);
        expect(config.propertyMap.get("key1")).toBe("value1");
        expect(config.propertyMap.get("key2")).toBe("value2");
    });

    test("EMPTY_SERVICE_CONFIGURATION should be empty",
         () => { expect(EMPTY_SERVICE_CONFIGURATION.propertyMap.size).toBe(0); });
});