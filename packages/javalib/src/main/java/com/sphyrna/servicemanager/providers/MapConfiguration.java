package com.sphyrna.servicemanager.providers;

import com.sphyrna.servicemanager.ServiceConfiguration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A default implementation of the {@link ServiceConfiguration}
 *
 * @author sgoldstein
 *
 */
public class MapConfiguration implements ServiceConfiguration
{
    private Map<String, Object> properties;

    public MapConfiguration()
    {
        this.properties = new HashMap<String, Object>();
    }

    @Override
    public Object getProperty(String name)
    {
        Objects.requireNonNull(name, "name cannot be null");

        if (!this.properties.containsKey(name))
        {
            throw new IllegalArgumentException(name + " is not a specified parameter");
        }

        return this.properties.get(name);
    }

    public void setProperty(String name, Object value)
    {
        Objects.requireNonNull(name, "name cannot be null");
        Objects.requireNonNull(value, "name cannot be value");

        this.properties.put(name, value);
    }

    @Override
    public boolean containsProperty(String name)
    {
        Objects.requireNonNull(name, "name cannot be null");

        return this.properties.containsKey(name);
    }
}
