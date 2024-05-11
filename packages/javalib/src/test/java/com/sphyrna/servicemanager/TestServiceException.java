package com.sphyrna.servicemanager;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import com.sphyrna.servicemanager.ServiceException;
import org.junit.jupiter.api.Test;

public class TestServiceException
{

    @Test
    public void testServiceExceptionString()
    {
        assertNotNull(new ServiceException("message"), "testServiceExceptionString - Ensure exception can be created");
    }

    @Test
    public void testServiceExceptionStringThrowable()
    {
        assertNotNull(new ServiceException("message", new Throwable()),
                      "testServiceExceptionStringThrowable - Ensure exception can be created");
    }

    @Test
    public void testServiceExceptionThrowable()
    {
        assertNotNull(new ServiceException(new Throwable()),
                      "testServiceExceptionThrowable - Ensure exception can be created");
    }
}
