/**
 * 
 */
package com.lifeease.pim.service.simple;

/**
 * @author sgoldstein
 * 
 */
public class MockSimpleServiceProvider extends SimpleServiceProvider
{

    /*
     * (non-Javadoc)
     * 
     * @see com.lifeease.pim.service.simple.SimpleServiceProvider#getServiceClass()
     */
    @Override
    protected Class<? extends SimpleService> getServiceClass()
    {
        return MockSimpleService.class;
    }

}
