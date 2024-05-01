package com.sphyrna.servicemanager;

import static org.junit.Assert.*;
import org.junit.Test;

import com.sphyrna.servicemanager.ServiceNotAvailableException;

public class TestServiceNotAvailableException
{

    @Test
    public void testServiceNotAvailableException()
    {
        assertNotNull(
                      "testServiceNotAvailableException - Ensure exception can be created",
                      new ServiceNotAvailableException("BadService"));

        // Test IllegalArgumentException for null value
        try
        {
            new ServiceNotAvailableException(null);
            fail("testServiceNotAvailableException - Specifying a null argument should lead to ILE");
        }
        catch (IllegalArgumentException exception)
        {
        }
    }

}
