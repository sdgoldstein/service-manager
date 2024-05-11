package com.sphyrna.servicemanager.defaultimpl;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sphyrna.servicemanager.ServiceException;
import org.junit.jupiter.api.Test;

public class TestServiceInstanceProviderImpl
{
    @Test
    public void testConstructorCreateServiceInstance() throws ServiceException
    {
        ServiceInstanceProviderImpl<TestService> provider =
            new ServiceInstanceProviderImpl<TestService>(TestService.class);
        TestService instance = (TestService)provider.createServiceInstance();

        assertTrue(instance.wasCalled, "testConstructorCreateServiceInstance - TestService constructor called");
        assertInstanceOf(TestService.class, instance,
                         "testConstructorCreateServiceInstance - Instance is of TestClass");
    }
}
