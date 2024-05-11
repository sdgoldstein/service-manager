package com.sphyrna.servicemanager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.sphyrna.servicemanager.providers.MapConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestMapConfiguration
{
    private MapConfiguration configurationToTest;

    @BeforeEach
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
        assertFalse(this.configurationToTest.containsProperty(propertyName),
                    "testGetPutContainsProperty - Ensure property does not exist");
        try
        {
            this.configurationToTest.getProperty(propertyName);
            fail("testGetPutContainsProperty - Should thrown IllegalArgumentException");
        }
        catch (IllegalArgumentException exception)
        {
        }

        // Now, put a property in
        this.configurationToTest.setProperty(propertyName, propertyValue);
        assertTrue(this.configurationToTest.containsProperty(propertyName),
                   "testGetPutContainsProperty - Ensure property does exists after adding");
        String retrievedValue = (String)this.configurationToTest.getProperty(propertyName);
        assertEquals(propertyValue, retrievedValue,
                     "testGetPutContainsProperty - Ensure property can be retrieved as expected");
    }
}
