/**
 * 
 */
package com.lifeease.pim.service.simple;

import com.lifeease.pim.service.Configuration;
import com.lifeease.pim.service.MapConfiguration;
import com.lifeease.pim.service.ServiceException;
import com.lifeease.pim.service.ServiceManager;
import com.lifeease.pim.service.ServiceManagerFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author sgoldstein
 * 
 */
public class TestSimpleServiceProvider
{

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception
    {
    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception
    {
    }

    /**
     * Test method for
     * {@link com.lifeease.pim.service.simple.SimpleServiceProvider#createService()}.
     * 
     * @throws ServiceException
     */
    @Test
    public void testCreateService() throws ServiceException
    {
        Configuration config = new MapConfiguration();
        ServiceManager serviceManager = ServiceManagerFactory.getInstance()
                                                             .getServiceManager();
        serviceManager.defineService("Foo",
                                     MockSimpleServiceProvider.class,
                                     config);

        MockSimpleService simpleService = (MockSimpleService) serviceManager.getService("Foo");

        try
        {
            serviceManager.defineService("Bar",
                                         BadSimpleServiceProvider.class,
                                         config);
        }
        catch (ServiceException exception)
        {
        }

        try
        {
            serviceManager.defineService("BarTwo",
                                         InAccessibleServiceProvider.class,
                                         config);
        }
        catch (ServiceException exception)
        {
        }

        serviceManager.shutdown();
    }

    public static class BadSimpleServiceProvider extends SimpleServiceProvider
    {
        /*
         * (non-Javadoc)
         * 
         * @see com.lifeease.pim.service.simple.SimpleServiceProvider#getServiceClass()
         */
        @Override
        protected Class<? extends SimpleService> getServiceClass()
        {
            return BadSimpleService.class;
        }
    }

    public static class BadSimpleService extends SimpleService
    {
        public BadSimpleService(String foo)
        {

        }
    }

    public static class InAccessibleServiceProvider extends
                                                   SimpleServiceProvider
    {
        /*
         * (non-Javadoc)
         * 
         * @see com.lifeease.pim.service.simple.SimpleServiceProvider#getServiceClass()
         */
        @Override
        protected Class<? extends SimpleService> getServiceClass()
        {
            return InAccessibleService.class;
        }
    }

    private static class InAccessibleService extends SimpleService
    {

    }
}
