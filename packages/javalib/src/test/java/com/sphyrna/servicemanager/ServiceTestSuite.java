package com.sphyrna.servicemanager;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.sphyrna.servicemanager.defaultimpl.TestServiceManagerImpl;
import com.sphyrna.servicemanager.providers.TestSingletonServiceProvider;

@RunWith(Suite.class)
@Suite.SuiteClasses( { TestServiceManagerImpl.class,
                      TestInvalidServiceDefinitionException.class,
                      TestMapConfiguration.class, TestServiceException.class,
                      TestServiceManagerFactory.class,
                      TestServiceNotAvailableException.class,
                      TestSingletonServiceProvider.class })
public class ServiceTestSuite
{

}
