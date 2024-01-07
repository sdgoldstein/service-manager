package com.lifeease.pim.service;

import java.util.HashMap;
import java.util.Map;

/**
 * A default implementation of the {@link Configuration}
 * 
 * @author sgoldstein
 * 
 */
public class MapConfiguration implements Configuration
{
    private Map<String, Object> properties;

    public MapConfiguration()
    {
        this.properties = new HashMap<String, Object>();
    }

    public Object getProperty(String name)
    {
        if (name == null)
        {
            throw new IllegalArgumentException("name cannot be null");
        }

        if (!this.properties.containsKey(name))
        {
            throw new IllegalArgumentException(name
                                               + " is not a specified parameter");
        }

        return this.properties.get(name);
    }

    public void putProperty(String name, Object value)
    {
        if (name == null)
        {
            throw new IllegalArgumentException("name cannot be null");
        }

        if (value == null)
        {
            throw new IllegalArgumentException("value cannot be null");
        }

        this.properties.put(name, value);
    }

    @Override
    public boolean containsProperty(String name)
    {
        if (name == null)
        {
            throw new IllegalArgumentException("name cannot be null");
        }

        return this.properties.containsKey(name);
    }
}