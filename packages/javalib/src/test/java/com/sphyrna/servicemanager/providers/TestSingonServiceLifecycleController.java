package com.sphyrna.servicemanager.providers;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sphyrna.servicemanager.Service;
import com.sphyrna.servicemanager.ServiceException;
import com.sphyrna.servicemanager.ServiceInstanceProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TestSingonServiceLifecycleController
{
    private static Service mockService;
    private static ServiceInstanceProvider<Service> mockServiceInstanceProvider;

    private static MapConfiguration testServiceConfiguration;

    private SingletonServiceLifecycleController controllerToTest;

    @BeforeAll
    public static void beforeAll() throws ServiceException
    {
        mockService = mock(Service.class);
        mockServiceInstanceProvider = mock(ServiceInstanceProvider.class);

        testServiceConfiguration = new MapConfiguration();
        testServiceConfiguration.setProperty("key1", "value1");
        testServiceConfiguration.setProperty("key2", "value2");
    }

    @BeforeEach
    public void beforeEach()
    {
        reset(mockService, mockServiceInstanceProvider);
        controllerToTest = new SingletonServiceLifecycleController();
    }

    @Test
    public void testSingonServiceLifecycleControllerImpl() throws ServiceException
    {
        when(mockServiceInstanceProvider.createServiceInstance()).thenReturn(mockService);

        // First call getService without calling init
        assertThrows(IllegalStateException.class, () -> { controllerToTest.getService(); });

        // Now, initialize and get service
        controllerToTest.init(this.mockServiceInstanceProvider, this.testServiceConfiguration);
        Service instance = controllerToTest.getService();
        assertTrue(
            instance == mockService,
            "testSingonServiceLifecycleControllerImpl - Ensure instance returned is the same as the mock service instance");

        verify(mockService).init(testServiceConfiguration);
        verify(mockService).start();
        verify(mockServiceInstanceProvider, times(1)).createServiceInstance();

        // Try to obtain it again
        reset(mockService);
        Service sameInstance = controllerToTest.getService();
        assertTrue(
            instance == mockService,
            "testSingonServiceLifecycleControllerImpl - Ensure instance returned is the same as the mock service instance 2");
        verify(mockService, never()).init(null);
        verify(mockService, never()).start();
        verify(mockServiceInstanceProvider, times(1)).createServiceInstance(); // Should still be one
    }

    @Test
    public void testShutdownStopDestroy() throws ServiceException
    {
        when(mockServiceInstanceProvider.createServiceInstance()).thenReturn(mockService);

        // Initialize and get the service started
        controllerToTest.init(mockServiceInstanceProvider, testServiceConfiguration);
        controllerToTest.getService();

        // Now, shutdown
        controllerToTest.shutdown();
        verify(mockService).stop();
        verify(mockService).destroy();

        // Once it's shutdown, expect methods to throw errors
        assertThrows(IllegalStateException.class, () -> { controllerToTest.getService(); });
    }
}
