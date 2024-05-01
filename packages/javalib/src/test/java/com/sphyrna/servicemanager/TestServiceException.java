package com.sphyrna.servicemanager;

import static org.junit.Assert.*;
import org.junit.Test;

import com.sphyrna.servicemanager.ServiceException;

public class TestServiceException
{

    @Test
    public void testServiceExceptionString()
    {
        assertNotNull(
                      "testServiceExceptionString - Ensure exception can be created",
                      new ServiceException("message"));

        // Test IllegalArgumentException for null value
        try
        {
            new ServiceException((String)null);
            fail("testServiceExceptionString - Specifying a null argument should lead to ILE");
        }
        catch (IllegalArgumentException exception)
        {
        }
    }

    @Test
    public void testServiceExceptionStringThrowable()
    {
        assertNotNull(
                      "testServiceExceptionStringThrowable - Ensure exception can be created",
                      new ServiceException("message", new Throwable()));

        // Test IllegalArgumentException for null value
        try
        {
            new ServiceException(null, new Throwable());
            fail("testServiceExceptionStringThrowable - Specifying a null message should lead to ILE");
        }
        catch (IllegalArgumentException exception)
        {
        }
        
        try
        {
            new ServiceException("message", null);
            fail("testServiceExceptionStringThrowable - Specifying a null throwable should lead to ILE");
        }
        catch (IllegalArgumentException exception)
        {
        }
    }

    @Test
    public void testServiceExceptionThrowable()  {
        assertNotNull(
                      "testServiceExceptionThrowable - Ensure exception can be created",
                      new ServiceException(new Throwable()));

        // Test IllegalArgumentException for null value
        try
        {
            new ServiceException((Throwable)null);
            fail("testServiceExceptionThrowable - Specifying a null argument should lead to ILE");
        }
        catch (IllegalArgumentException exception)
        {
        }
    }

}
