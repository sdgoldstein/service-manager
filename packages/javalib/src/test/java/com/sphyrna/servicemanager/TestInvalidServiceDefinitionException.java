package com.sphyrna.servicemanager;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

public class TestInvalidServiceDefinitionException
{

    @Test
    public void testInvalidServiceExceptionString()
    {
        assertNotNull(new InvalidServiceDefinitionException("message"),
                      "testInvalidServiceExceptionString - Ensure exception can be created");
    }

    @Test
    public void testInvalidServiceExceptionStringThrowable()
    {
        assertNotNull(new InvalidServiceDefinitionException("message", new Throwable()),
                      "testInvalidServiceExceptionStringThrowable - Ensure exception can be created");
    }

    @Test
    public void testInvalidServiceExceptionThrowable()
    {
        assertNotNull(new InvalidServiceDefinitionException(new Throwable()),
                      "testInvalidServiceExceptionThrowable - Ensure exception can be created");
    }
}
