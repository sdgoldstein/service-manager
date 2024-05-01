package com.sphyrna.servicemanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.sphyrna.servicemanager.providers.MapConfiguration;
import org.junit.Before;
import org.junit.Test;

public class TestMapConfiguration
{
    private MapConfiguration configurationToTest;

    @Before
    public void setUp() throws Exception
    {
        this.configurationToTest = new MapConfiguration();
    }

    @Test
    public void testMapConfiguration()
    {
        // Simply call it
        new MapConfiguration();
    }

    @Test
    public void testGetPutContainsProperty()
    {
        String propertyName = "propName";
        String propertyValue = "propValue";

        // First ensure state when property does not exists
        assertFalse("testGetPutContainsProperty - Ensure property does not exist",
                    this.configurationToTest.containsProperty(propertyName));
        try
        {
            this.configurationToTest.getProperty(propertyName);
            fail("testGetPutContainsProperty - Should thrown IllegalArgumentException");
        }
        catch (IllegalArgumentException exception)
        {
        }

        // Now, put a property in
        this.configurationToTest.putProperty(propertyName, propertyValue);
        assertTrue("testGetPutContainsProperty - Ensure property does exists after adding",
                   this.configurationToTest.containsProperty(propertyName));
        String retrievedValue = (String)this.configurationToTest.getProperty(propertyName);
        assertEquals("testGetPutContainsProperty - Ensure property can be retrieved as expected", propertyValue,
                     retrievedValue);

        // Test bad args
        try
        {
            this.configurationToTest.getProperty(null);
            fail(
                "testGetPutContainsProperty - Should thrown IllegalArgumentException for null argument to getProperty");
        }
        catch (IllegalArgumentException exception)
        {
        }

        try
        {
            this.configurationToTest.putProperty(null, propertyValue);
            fail(
                "testGetPutContainsProperty - Should thrown IllegalArgumentException for null name argument to putProperty");
        }
        catch (IllegalArgumentException exception)
        {
        }

        try
        {
            this.configurationToTest.putProperty(propertyName, null);
            fail(
                "testGetPutContainsProperty - Should thrown IllegalArgumentException for null name value to putProperty");
        }
        catch (IllegalArgumentException exception)
        {
        }

        try
        {
            this.configurationToTest.containsProperty(null);
            fail(
                "testGetPutContainsProperty - Should thrown IllegalArgumentException for null argument to containsProperty");
        }
        catch (IllegalArgumentException exception)
        {
        }
    }
}
