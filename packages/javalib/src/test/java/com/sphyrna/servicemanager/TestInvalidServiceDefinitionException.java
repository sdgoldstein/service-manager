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

        // Test IllegalArgumentException for null value
        try
        {
            new InvalidServiceDefinitionException((String)null);
            fail("testInvalidServiceExceptionString - Specifying a null argument should lead to ILE");
        }
        catch (IllegalArgumentException exception)
        {
        }
    }

    @Test
    public void testInvalidServiceExceptionStringThrowable()
    {
        assertNotNull(new InvalidServiceDefinitionException("message", new Throwable()),
                      "testInvalidServiceExceptionStringThrowable - Ensure exception can be created");

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
        assertNotNull(new InvalidServiceDefinitionException(new Throwable()),
                      "testInvalidServiceExceptionThrowable - Ensure exception can be created");

        // Test IllegalArgumentException for null value
        try
        {
            new InvalidServiceDefinitionException((Throwable)null);
            fail("testInvalidServiceExceptionThrowable - Specifying a null throwable");
        }
        catch (IllegalArgumentException exception)
        {
        }
    }
}
