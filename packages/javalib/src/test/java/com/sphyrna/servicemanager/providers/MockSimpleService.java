/**
 *
 */
package com.sphyrna.servicemanager.providers;

import com.sphyrna.servicemanager.ServiceConfiguration;
import com.sphyrna.servicemanager.ServiceException;
import com.sphyrna.servicemanager.providers.BaseService;

/**
 * @author sgoldstein
 *
 */
public class MockSimpleService extends BaseService
{

    @Override
    protected void destroy() throws ServiceException
    {
        // TODO Auto-generated method stub
        super.destroy();
    }

    @Override
    protected void init(ServiceConfiguration configuration) throws ServiceException
    {
        // TODO Auto-generated method stub
        super.init(configuration);
    }

    @Override
    protected void start() throws ServiceException
    {
        // TODO Auto-generated method stub
        super.start();
    }

    @Override
    protected void stop() throws ServiceException
    {
        // TODO Auto-generated method stub
        super.stop();
    }
}
