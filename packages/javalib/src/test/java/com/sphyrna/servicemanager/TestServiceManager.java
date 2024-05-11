package com.sphyrna.servicemanager;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

import com.sphyrna.servicemanager.providers.MapConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TestServiceManager
{
    @Mock
    private ServiceManagerStrategy mockStrategy;

    @BeforeEach
    public void beforeEach()
    {
        reset(mockStrategy);
    }

    @Test
    public void testSetDefaultStrategyGetService() throws ServiceNotAvailableException, ServiceException
    {
        ServiceManager.setDefaultStrategy(this.mockStrategy);
        ServiceManager.getService("someService");
        verify(this.mockStrategy).getService("someService");

        // Test with a config
        MapConfiguration serviceConfig = new MapConfiguration();
        serviceConfig.setProperty("foo", "bar");
        ServiceManager.getService("someOtherService", serviceConfig);
        verify(mockStrategy).getService("someOtherService", serviceConfig);
    }

    @Test
    public void testSetDefaultStrategyIsServiceDefined()
    {
        ServiceManager.setDefaultStrategy(mockStrategy);
        ServiceManager.isServiceDefined("someService");
        verify(mockStrategy).isServiceDefined("someService");
    }

    @Test
    public void testShutdown()
    {
        ServiceManager.setDefaultStrategy(mockStrategy);
        ServiceManager.shutdown();
        verify(mockStrategy).shutdown();
    }
}
