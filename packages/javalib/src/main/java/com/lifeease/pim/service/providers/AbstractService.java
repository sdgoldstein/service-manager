/**
 * 
 */
package com.lifeease.pim.service.providers;

import com.lifeease.pim.service.Configuration;
import com.lifeease.pim.service.Service;
import com.lifeease.pim.service.ServiceException;

import java.util.logging.Logger;

/**
 * @author sgoldstein
 * 
 */
public abstract class AbstractService implements Service
{

    /**
     * 
     */
    protected void destroy() throws ServiceException
    {
        // TODO Auto-generated method stub

    }

    /**
     * @param configuration
     */
    protected void init(Configuration configuration) throws ServiceException
    {
        // TODO Auto-generated method stub

    }

    /**
     * 
     */
    protected void start() throws ServiceException
    {
        // TODO Auto-generated method stub

    }

    /**
     * 
     */
    protected void stop() throws ServiceException
    {
        // TODO Auto-generated method stub
    }

    protected Logger getLogger()
    {
        return Logger.getLogger(this.getClass().getName());
    }
}
