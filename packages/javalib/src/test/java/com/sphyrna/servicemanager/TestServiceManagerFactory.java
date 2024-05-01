package com.sphyrna.servicemanager;

import static org.junit.Assert.assertNotNull;
import org.junit.Test;

import com.sphyrna.servicemanager.ServiceManagerFactory;

public class TestServiceManagerFactory
{

    @Test
    public void testGetInstance()
    {
        assertNotNull(ServiceManagerFactory.getInstance());
    }

    @Test
    public void testGetServiceManager()
    {
        assertNotNull(ServiceManagerFactory.getInstance().getServiceManager());
    }

}
