package com.sphyrna.servicemanager;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

public class TestServiceNotAvailableException
{
    @Test
    public void testServiceNotAvailableException()
    {
        assertNotNull(new ServiceNotAvailableException("BadService"),
                      "testServiceNotAvailableException - Ensure exception can be created");
    }
}
