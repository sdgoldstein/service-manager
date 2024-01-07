package com.lifeease.pim.service;

import static org.junit.Assert.assertNotNull;
import org.junit.Test;

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
