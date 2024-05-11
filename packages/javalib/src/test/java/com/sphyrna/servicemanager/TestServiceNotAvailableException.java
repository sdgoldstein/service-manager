package com.sphyrna.servicemanager;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import com.sphyrna.servicemanager.ServiceNotAvailableException;
import org.junit.jupiter.api.Test;

public class TestServiceNotAvailableException
{

    @Test
    public void testServiceNotAvailableException()
    {
        assertNotNull(new ServiceNotAvailableException("BadService"),
                      "testServiceNotAvailableException - Ensure exception can be created");

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
