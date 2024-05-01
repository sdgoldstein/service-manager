package com.sphyrna.servicemanager.defaultimpl;

import com.sphyrna.servicemanager.Service;
import com.sphyrna.servicemanager.ServiceConfiguration;
import com.sphyrna.servicemanager.ServiceException;
import com.sphyrna.servicemanager.ServiceLifecycleController;
import com.sphyrna.servicemanager.ServiceNotAvailableException;
import com.sphyrna.servicemanager.ServiceProvider;
import com.sphyrna.servicemanager.defaultimpl.ServiceManagerStrategyImpl;
import com.sphyrna.servicemanager.providers.MapConfiguration;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * ServiceManagerImpl Test.
 */
public class TestServiceManagerImpl extends TestCase
{
    private ServiceManagerStrategyImpl serviceManagerToTest;

    public TestServiceManagerImpl(String name)
    {
        super(name);
    }

    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        this.serviceManagerToTest = new ServiceManagerStrategyImpl();
    }

    @Override
    public void tearDown() throws Exception
    {
        super.tearDown();
        this.serviceManagerToTest.shutdown();
    }

    public void testGetService() throws Exception
    {
        String goodServiceName = "goodService";
        ServiceConfiguration config = new MapConfiguration();
        this.serviceManagerToTest.defineService(goodServiceName, GoodServiceProvider.class, config);
        Service goodService = this.serviceManagerToTest.getService(goodServiceName);
        assertNotNull("Ensure that good service retrieved is not null.", goodService);

        // Test with illegal arguments
        try
        {
            this.serviceManagerToTest.getService("UknownService");
            fail("Should throw ServiceNotFoundExcepotion");
        }
        catch (ServiceNotAvailableException exception)
        {
        }

        try
        {
            this.serviceManagerToTest.getService(null);
            fail("Should throw IllegalArgumentExcepotion");
        }
        catch (IllegalArgumentException exception)
        {
        }
    }

    public void testIsServiceAvailable() throws Exception
    {
        String goodServiceName = "goodService";
        ServiceConfiguration config = new MapConfiguration();
        this.serviceManagerToTest.defineService(goodServiceName, GoodServiceProvider.class, config);

        assertTrue("Ensure that good service is available.",
                   this.serviceManagerToTest.isServiceAvailable(goodServiceName));

        assertFalse("Ensure that bad service name is not available",
                    this.serviceManagerToTest.isServiceAvailable("UknownService"));

        try
        {
            this.serviceManagerToTest.isServiceAvailable(null);
            fail("Should throw IllegalArgumentExcepotion");
        }
        catch (IllegalArgumentException exception)
        {
        }
    }

    public void testDefineService() throws Exception
    {
        String goodServiceName = "goodService";
        ServiceConfiguration config = new MapConfiguration();
        this.serviceManagerToTest.defineService(goodServiceName, GoodServiceProvider.class, config);
        GoodService goodService = (GoodService)this.serviceManagerToTest.getService(goodServiceName);
        assertNotNull("Ensure that good service can be retrieved.", goodService);
        assertTrue("Ensure good service was configured.", goodService.wasConfigured(config));
        assertTrue("Ensure good service was initialized.", goodService.wasInitialized());

        // Test bad services
        try
        {
            this.serviceManagerToTest.defineService("BadNoConstructorService", BadNoConstructorServiceProvider.class,
                                                    config);
            fail("Should throw IllegalArgumentException for bad constructor");
        }
        catch (IllegalArgumentException exception)
        {
        }

        try
        {
            this.serviceManagerToTest.defineService("InaccessibleService", InaccessibleServiceProvider.class, config);
            fail("Should throw IllegalArgymentException for inaccessible service");
        }
        catch (IllegalArgumentException exception)
        {
        }

        // FIX ME - Test with Bad Services!

        // Try defining the same service again
        try
        {
            this.serviceManagerToTest.defineService(goodServiceName, GoodServiceProvider.class, config);

            fail("Should throw IllegalStateException");
        }
        catch (IllegalStateException exception)
        {
        }

        // Test null arguments
        try
        {
            this.serviceManagerToTest.defineService(null, GoodServiceProvider.class, config);
            fail("Should throw IllegalArgumentExcepotion");
        }
        catch (IllegalArgumentException exception)
        {
        }

        try
        {
            this.serviceManagerToTest.defineService(goodServiceName, null, config);
            fail("Should throw IllegalArgumentExcepotion");
        }
        catch (IllegalArgumentException exception)
        {
        }

        try
        {
            this.serviceManagerToTest.defineService(goodServiceName, GoodServiceProvider.class, null);
            fail("Should throw IllegalArgumentExcepotion");
        }
        catch (IllegalArgumentException exception)
        {
        }
    }

    public void testDefineServiceNameClass() throws Exception
    {
        String goodServiceName = "goodService";
        this.serviceManagerToTest.defineService(goodServiceName, GoodServiceProvider.class);
        GoodService goodService = (GoodService)this.serviceManagerToTest.getService(goodServiceName);
        assertNotNull("Ensure that good service can be retrieved.", goodService);
        assertTrue("Ensure good service was initialized.", goodService.wasInitialized());

        // Test bad services
        try
        {
            this.serviceManagerToTest.defineService("BadNoConstructorService", BadNoConstructorServiceProvider.class);
            fail("Should throw IllegalArgumentException for bad constructor");
        }
        catch (IllegalArgumentException exception)
        {
        }

        try
        {
            this.serviceManagerToTest.defineService("InaccessibleService", InaccessibleServiceProvider.class);
            fail("Should throw IllegalArgumentException for inaccessible service");
        }
        catch (IllegalArgumentException exception)
        {
        }

        // FIX ME - Test with Bad Services!

        // Try defining the same service again
        try
        {
            this.serviceManagerToTest.defineService(goodServiceName, GoodServiceProvider.class);

            fail("Should throw IllegalStateException");
        }
        catch (IllegalStateException exception)
        {
        }

        // Test null arguments
        try
        {
            this.serviceManagerToTest.defineService(null, GoodServiceProvider.class);
            fail("Should throw IllegalArgumentExcepotion");
        }
        catch (IllegalArgumentException exception)
        {
        }

        try
        {
            this.serviceManagerToTest.defineService(goodServiceName, (Class)null);
            fail("Should throw IllegalArgumentExcepotion");
        }
        catch (IllegalArgumentException exception)
        {
        }
    }

    public void testShutdown() throws ServiceException
    {
        this.serviceManagerToTest.shutdown();

        // Now, try with a service for which the destroy method throws an
        // exception
        this.serviceManagerToTest.defineService("foo", FailedDestroyServiceProvider.class);

        this.serviceManagerToTest.shutdown();
    }

    public static Test suite()
    {
        return new TestSuite(TestServiceManagerImpl.class);
    }

    public void main(String[] args)
    {
        junit.textui.TestRunner.run(suite());
    }

    public static class GoodServiceProvider implements ServiceProvider
    {

        @Override
        public ServiceLifecycleController createService()
        {
            // TODO Auto-generated method stub
            return new GoodServiceController();
        }
    }

    public static class BadNoConstructorServiceProvider implements ServiceProvider
    {
        public BadNoConstructorServiceProvider(String arg) {}

        @Override
        public ServiceLifecycleController createService()
        {
            // TODO Auto-generated method stub
            return null;
        }
    }

    public static class InaccessibleServiceProvider implements ServiceProvider
    {
        private InaccessibleServiceProvider() {}

        @Override
        public ServiceLifecycleController createService()
        {
            // TODO Auto-generated method stub
            return null;
        }
    }

    private static class GoodServiceController implements ServiceLifecycleController
    {
        private GoodService goodService = new GoodService();

        /*
         * (non-Javadoc)
         *
         * @see com.lifeease.pim.service.ServiceLiaison#destroyService()
         */
        @Override
        public void destroyService() throws ServiceException
        {
            // TODO Auto-generated method stub
        }

        /*
         * (non-Javadoc)
         *
         * @see com.lifeease.pim.service.ServiceLiaison#getService()
         */
        @Override
        public Service getService()
        {
            return this.goodService;
        }

        /*
         * (non-Javadoc)
         *
         * @see com.lifeease.pim.service.ServiceLiaison#initService(com.lifeease.pim.service.Configuration)
         */
        @Override
        public void initService(ServiceConfiguration configuration) throws ServiceException
        {
            this.goodService.setConfiguration(configuration);
            this.goodService.init();
        }

        /*
         * (non-Javadoc)
         *
         * @see com.lifeease.pim.service.ServiceLiaison#startService()
         */
        @Override
        public void startService() throws ServiceException
        {
            this.goodService.start();
        }

        /*
         * (non-Javadoc)
         *
         * @see com.lifeease.pim.service.ServiceLiaison#stopService()
         */
        @Override
        public void stopService() throws ServiceException
        {
            this.goodService.start();
        }
    }

    private static class GoodService implements Service
    {
        private ServiceConfiguration config;
        private boolean wasInitialized;
        private boolean wasStarted;

        public void setConfiguration(ServiceConfiguration configuration)
        {
            this.config = configuration;
        }

        public void init()
        {
            this.wasInitialized = true;
        }

        public void start()
        {
            this.wasStarted = true;
        }

        public void stop() {}

        public void destroy()
        {
            // To change body of implemented methods use File | Settings |
            // File
            // Templates.
        }

        public boolean wasConfigured(ServiceConfiguration config)
        {
            return this.config == config;
        }

        public boolean wasInitialized()
        {
            return this.wasInitialized;
        }

        public boolean wasStarted()
        {
            return this.wasStarted;
        }
    }

    public static class FailedDestroyServiceProvider implements ServiceProvider
    {

        /*
         * (non-Javadoc)
         *
         * @see com.lifeease.pim.service.ServiceProvider#createService()
         */
        @Override
        public ServiceLifecycleController createService()
        {
            return new ServiceLifecycleController() {
                @Override
                public void destroyService() throws ServiceException
                {
                    throw new ServiceException("Failed to destroy");
                }

                @Override
                public Service getService()
                {
                    return new GoodService();
                }

                @Override
                public void initService(ServiceConfiguration configuration) throws ServiceException
                {
                }

                @Override
                public void startService() throws ServiceException
                {
                    // TODO Auto-generated method stub
                }

                @Override
                public void stopService() throws ServiceException
                {
                    // TODO Auto-generated method stub
                }
            };
        }
    }
}
