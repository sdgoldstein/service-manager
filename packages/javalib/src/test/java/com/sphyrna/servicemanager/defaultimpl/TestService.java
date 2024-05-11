package com.sphyrna.servicemanager.defaultimpl;

import com.sphyrna.servicemanager.Service;
import com.sphyrna.servicemanager.ServiceConfiguration;
import com.sphyrna.servicemanager.providers.BaseService;

public class TestService<C extends ServiceConfiguration> extends BaseService<C> implements Service<C>
{
    boolean wasCalled = false;
    TestService()
    {
        super();
        this.wasCalled = true;
    }
}