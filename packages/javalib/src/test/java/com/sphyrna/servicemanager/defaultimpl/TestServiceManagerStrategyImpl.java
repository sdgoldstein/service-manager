package com.sphyrna.servicemanager.defaultimpl;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sphyrna.servicemanager.Service;
import com.sphyrna.servicemanager.ServiceConfiguration;
import com.sphyrna.servicemanager.ServiceException;
import com.sphyrna.servicemanager.ServiceInstanceProvider;
import com.sphyrna.servicemanager.ServiceLifecycleController;
import com.sphyrna.servicemanager.ServiceNotAvailableException;
import com.sphyrna.servicemanager.providers.MapConfiguration;
import com.sphyrna.servicemanager.providers.SingletonServiceLifecycleController;
import javax.naming.directory.InvalidSearchControlsException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TestServiceManagerStrategyImpl
{
    private static Service mockService;
    private static ServiceInstanceProvider<Service> mockServiceInstanceProvider;
    private static MapConfiguration testServiceConfiguration;
    private static ServiceLifecycleController mockServiceLifecycleController;

    private ServiceManagerStrategyImpl strategyToTest;

    @BeforeAll
    public static void beforeAll() throws ServiceException
    {
        mockService = mock(Service.class);
        mockServiceInstanceProvider = mock(ServiceInstanceProvider.class);
        mockServiceLifecycleController = mock(ServiceLifecycleController.class);

        testServiceConfiguration = new MapConfiguration();
        testServiceConfiguration.setProperty("key1", "value1");
        testServiceConfiguration.setProperty("key2", "value2");
    }

    @BeforeEach
    public void beforeEach()
    {
        reset(mockService, mockServiceInstanceProvider, mockServiceLifecycleController);
        strategyToTest = new ServiceManagerStrategyImpl();
    }

    @Test
    public void testRegisterServiceIsServiceDefinedGetService() throws ServiceException
    {
        String serviceNameOne = "serviceNameOne";
        assertFalse(strategyToTest.isServiceDefined(serviceNameOne));
        assertThrows(ServiceNotAvailableException.class, () -> { strategyToTest.getService(serviceNameOne); });

        // register a service
        strategyToTest.registerService(serviceNameOne, mockServiceInstanceProvider, mockServiceLifecycleController,
                                       testServiceConfiguration);
        assertTrue(strategyToTest.isServiceDefined(serviceNameOne));
        strategyToTest.getService(serviceNameOne);
        verify(mockServiceLifecycleController).init(mockServiceInstanceProvider, testServiceConfiguration);
        verify(mockServiceLifecycleController).getService();

        // Override a service
        reset(mockServiceLifecycleController);
        strategyToTest.registerService(serviceNameOne, mockServiceInstanceProvider, mockServiceLifecycleController,
                                       testServiceConfiguration, true);
        assertTrue(strategyToTest.isServiceDefined(serviceNameOne));
        strategyToTest.getService(serviceNameOne);
        verify(mockServiceLifecycleController).init(mockServiceInstanceProvider, testServiceConfiguration);
        verify(mockServiceLifecycleController).getService();

        // test overriding with registering with service with controller only
        reset(mockServiceLifecycleController);
        strategyToTest.registerServiceByControllerOnly(serviceNameOne, mockServiceLifecycleController,
                                                       testServiceConfiguration, true);
        assertTrue(strategyToTest.isServiceDefined(serviceNameOne));
        strategyToTest.getService(serviceNameOne);
        verify(mockServiceLifecycleController, never()).init(mockServiceInstanceProvider, testServiceConfiguration);
        verify(mockServiceLifecycleController).getService();

        // test service error for false override
        assertThrows(IllegalArgumentException.class, () -> {
            strategyToTest.registerService(serviceNameOne, mockServiceInstanceProvider, mockServiceLifecycleController,
                                           testServiceConfiguration, false);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            strategyToTest.registerService(serviceNameOne, mockServiceInstanceProvider, mockServiceLifecycleController,
                                           testServiceConfiguration);
        });
    }

    @Test
    public void testRegisterServiceByControllerOnlyIsServiceDefinedGetService() throws ServiceException
    {
        String serviceNameOne = "serviceNameOne";
        assertFalse(strategyToTest.isServiceDefined(serviceNameOne));
        assertThrows(ServiceNotAvailableException.class, () -> { strategyToTest.getService(serviceNameOne); });

        // register a service
        strategyToTest.registerServiceByControllerOnly(serviceNameOne, mockServiceLifecycleController,
                                                       testServiceConfiguration);
        assertTrue(strategyToTest.isServiceDefined(serviceNameOne));
        strategyToTest.getService(serviceNameOne);
        verify(mockServiceLifecycleController, never()).init(mockServiceInstanceProvider, testServiceConfiguration);
        verify(mockServiceLifecycleController).getService();

        // Override a service
        reset(mockServiceLifecycleController);
        strategyToTest.registerServiceByControllerOnly(serviceNameOne, mockServiceLifecycleController,
                                                       testServiceConfiguration, true);
        assertTrue(strategyToTest.isServiceDefined(serviceNameOne));
        strategyToTest.getService(serviceNameOne);
        verify(mockServiceLifecycleController, never()).init(mockServiceInstanceProvider, testServiceConfiguration);
        verify(mockServiceLifecycleController).getService();

        // test overriding with registering with service with provider and config
        reset(mockServiceLifecycleController);
        String serviceNameTwo = "serviceNameTwo";
        strategyToTest.registerServiceByControllerOnly(serviceNameTwo, mockServiceLifecycleController,
                                                       testServiceConfiguration);
        assertTrue(strategyToTest.isServiceDefined(serviceNameTwo));
        strategyToTest.registerService(serviceNameTwo, mockServiceInstanceProvider, mockServiceLifecycleController,
                                       testServiceConfiguration, true);
        assertTrue(strategyToTest.isServiceDefined(serviceNameTwo));
        strategyToTest.getService(serviceNameTwo);
        verify(mockServiceLifecycleController).init(mockServiceInstanceProvider, testServiceConfiguration);
        verify(mockServiceLifecycleController).getService();

        // test service error for false override
        assertThrows(IllegalArgumentException.class, () -> {
            strategyToTest.registerServiceByControllerOnly(serviceNameOne, mockServiceLifecycleController,
                                                           testServiceConfiguration, false);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            strategyToTest.registerServiceByControllerOnly(serviceNameOne, mockServiceLifecycleController,
                                                           testServiceConfiguration);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            strategyToTest.registerServiceByControllerOnly(serviceNameTwo, mockServiceLifecycleController,
                                                           testServiceConfiguration);
        });
    }

    @Test
    public void testRegisterServiceByClassIsServiceDefinedGetService() throws ServiceException
    {
        String serviceNameOne = "serviceNameOne";
        assertFalse(strategyToTest.isServiceDefined(serviceNameOne));
        assertThrows(ServiceNotAvailableException.class, () -> { strategyToTest.getService(serviceNameOne); });

        // register a service
        strategyToTest.registerServiceByClass(serviceNameOne, TestService.class,
                                              SingletonServiceLifecycleController.class, testServiceConfiguration);
        assertTrue(strategyToTest.isServiceDefined(serviceNameOne));
        strategyToTest.getService(serviceNameOne);

        // Override a service
        reset(mockServiceLifecycleController);
        strategyToTest.registerServiceByClass(serviceNameOne, TestService.class,
                                              SingletonServiceLifecycleController.class, testServiceConfiguration,
                                              true);
        assertTrue(strategyToTest.isServiceDefined(serviceNameOne));
        strategyToTest.getService(serviceNameOne);

        // test overriding with registering with service with controller only
        reset(mockServiceLifecycleController);
        String serviceNameTwo = "serviceNameTwo";
        strategyToTest.registerServiceByClass(serviceNameTwo, TestService.class,
                                              SingletonServiceLifecycleController.class, testServiceConfiguration);
        assertTrue(strategyToTest.isServiceDefined(serviceNameTwo));
        strategyToTest.registerServiceByControllerOnly(serviceNameTwo, mockServiceLifecycleController,
                                                       testServiceConfiguration, true);
        assertTrue(strategyToTest.isServiceDefined(serviceNameOne));
        strategyToTest.getService(serviceNameTwo);
        verify(mockServiceLifecycleController, never()).init(mockServiceInstanceProvider, testServiceConfiguration);
        verify(mockServiceLifecycleController).getService();

        // test service error for false override
        assertThrows(IllegalArgumentException.class, () -> {
            strategyToTest.registerServiceByClass(serviceNameOne, TestService.class,
                                                  SingletonServiceLifecycleController.class, testServiceConfiguration,
                                                  false);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            strategyToTest.registerServiceByClass(serviceNameOne, TestService.class,
                                                  SingletonServiceLifecycleController.class, testServiceConfiguration);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            strategyToTest.registerServiceByClass(serviceNameTwo, TestService.class,
                                                  SingletonServiceLifecycleController.class, testServiceConfiguration);
        });
    }

    @Test
    public void testRegisterSingletonServiceIsServiceDefinedGetService() throws ServiceException
    {
        String serviceNameOne = "serviceNameOne";
        assertFalse(strategyToTest.isServiceDefined(serviceNameOne));
        assertThrows(ServiceNotAvailableException.class, () -> { strategyToTest.getService(serviceNameOne); });

        // register a service
        strategyToTest.registerSingletonService(serviceNameOne, TestService.class, testServiceConfiguration);
        assertTrue(strategyToTest.isServiceDefined(serviceNameOne));
        Service serviceOne = strategyToTest.getService(serviceNameOne);
        assertInstanceOf(TestService.class, serviceOne);

        // Verify singleton behavior
        Service serviceOneAgain = strategyToTest.getService(serviceNameOne);
        assertTrue(serviceOne == serviceOneAgain);

        // Override a service
        strategyToTest.registerSingletonService(serviceNameOne, TestService.class, testServiceConfiguration, true);
        assertTrue(strategyToTest.isServiceDefined(serviceNameOne));
        serviceOne = strategyToTest.getService(serviceNameOne);
        assertInstanceOf(TestService.class, serviceOne);

        // test overriding with registering with service with controller only
        reset(mockServiceLifecycleController);
        String serviceNameTwo = "serviceNameTwo";
        strategyToTest.registerSingletonService(serviceNameTwo, TestService.class, testServiceConfiguration);
        assertTrue(strategyToTest.isServiceDefined(serviceNameTwo));
        strategyToTest.registerServiceByControllerOnly(serviceNameTwo, mockServiceLifecycleController,
                                                       testServiceConfiguration, true);
        assertTrue(strategyToTest.isServiceDefined(serviceNameTwo));
        strategyToTest.getService(serviceNameTwo);
        verify(mockServiceLifecycleController, never()).init(mockServiceInstanceProvider, testServiceConfiguration);
        verify(mockServiceLifecycleController).getService();

        // test service error for false override
        assertThrows(IllegalArgumentException.class, () -> {
            strategyToTest.registerSingletonService(serviceNameOne, TestService.class, testServiceConfiguration, false);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            strategyToTest.registerSingletonService(serviceNameOne, TestService.class, testServiceConfiguration);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            strategyToTest.registerSingletonService(serviceNameTwo, TestService.class, testServiceConfiguration);
        });
    }

    @Test
    public void testShutdown() throws ServiceException
    {
        String serviceNameOne = "serviceNameOne";

        // register a service
        strategyToTest.registerService(serviceNameOne, mockServiceInstanceProvider, mockServiceLifecycleController,
                                       testServiceConfiguration);
        assertTrue(strategyToTest.isServiceDefined(serviceNameOne));
        strategyToTest.getService(serviceNameOne);

        strategyToTest.shutdown();
        verify(mockServiceLifecycleController).shutdown();

        // Once it's shutdown, expect methods to throw errors
        assertThrows(ServiceNotAvailableException.class, () -> { strategyToTest.getService(serviceNameOne); });
    }
}
