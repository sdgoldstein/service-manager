/**
 * 
 */
package com.lifeease.pim.service.simple;

import com.lifeease.pim.service.Configuration;
import com.lifeease.pim.service.Service;
import java.util.logging.Logger;

/**
 * @author sgoldstein
 * 
 */
public abstract class SimpleService implements Service
{

    /**
     * 
     */
    protected void destroy()
    {
        // TODO Auto-generated method stub

    }

    /**
     * @param configuration
     */
    protected void init(Configuration configuration)
    {
        // TODO Auto-generated method stub

    }

    /**
     * 
     */
    protected void start()
    {
        // TODO Auto-generated method stub

    }

    /**
     * 
     */
    protected void stop()
    {
        // TODO Auto-generated method stub
    }

    protected Logger getLogger()
    {
        return Logger.getLogger(this.getClass().getName());
    }
}
