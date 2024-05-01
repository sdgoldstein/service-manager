/**
 *
 */
package com.sphyrna.servicemanager.providers;

import com.sphyrna.servicemanager.*;
import com.sphyrna.servicemanager.providers.SingletonServiceLifecycleController;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author sgoldstein
 *
 */
public class TestSingletonServiceProvider
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
     * {@link com.lifeease.pim.service.providers.AbstractServiceProvider#createService()}.
     *
     * @throws ServiceException
     */
    @Test
    public void testCreateService() throws ServiceException
    {
        ServiceManager serviceManager = ServiceManagerFactory.getInstance().getServiceManager();
        serviceManager.defineService(
            "Foo", SingletonServiceLifecycleController.createSingletonSeviceProvider(MockSimpleService.class));

        MockSimpleService simpleService = (MockSimpleService)serviceManager.getService("Foo");

        serviceManager.shutdown();
    }
}
