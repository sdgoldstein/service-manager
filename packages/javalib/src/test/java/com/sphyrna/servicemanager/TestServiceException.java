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
        assertNotNull(new ServiceException("message", new Throwable()),
                      "testServiceExceptionStringThrowable - Ensure exception can be created");

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
    public void testServiceExceptionThrowable()
    {
        assertNotNull(

            new ServiceException(new Throwable()), "testServiceExceptionThrowable - Ensure exception can be created");

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
