package com.lifeease.pim.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import org.junit.Test;

public class TestInvalidServiceDefinitionException
{

    @Test
    public void testInvalidServiceExceptionString()
    {
        assertNotNull("testInvalidServiceExceptionString - Ensure exception can be created",
                      new InvalidServiceDefinitionException("message"));

        // Test IllegalArgumentException for null value
        try
        {
            new InvalidServiceDefinitionException((String) null);
            fail("testInvalidServiceExceptionString - Specifying a null argument should lead to ILE");
        }
        catch (IllegalArgumentException exception)
        {
        }
    }

    @Test
    public void testInvalidServiceExceptionStringThrowable()
    {
        assertNotNull("testInvalidServiceExceptionStringThrowable - Ensure exception can be created",
                      new InvalidServiceDefinitionException("message",
                                                            new Throwable()));

        // Test IllegalArgumentException for null value
        try
        {
            new InvalidServiceDefinitionException(null, new Throwable());
            fail("testInvalidServiceExceptionStringThrowable - Specifying a null message should lead to ILE");
        }
        catch (IllegalArgumentException exception)
        {
        }

        try
        {
            new InvalidServiceDefinitionException("message", null);
            fail("testInvalidServiceExceptionStringThrowable - Specifying a null throwable should lead to ILE");
        }
        catch (IllegalArgumentException exception)
        {
        }
    }

    @Test
    public void testInvalidServiceExceptionThrowable()
    {
        assertNotNull("testInvalidServiceExceptionThrowable - Ensure exception can be created",
                      new InvalidServiceDefinitionException(new Throwable()));

        // Test IllegalArgumentException for null value
        try
        {
            new InvalidServiceDefinitionException((Throwable) null);
            fail("testInvalidServiceExceptionThrowable - Specifying a null throwable");
        }
        catch (IllegalArgumentException exception)
        {
        }
    }
}
