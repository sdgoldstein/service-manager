package com.lifeease.pim.service;

import com.lifeease.pim.service.defaultimpl.TestServiceManagerImpl;
import com.lifeease.pim.service.simple.TestSimpleServiceProvider;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( { TestServiceManagerImpl.class,
                      TestInvalidServiceDefinitionException.class,
                      TestMapConfiguration.class, TestServiceException.class,
                      TestServiceManagerFactory.class,
                      TestServiceNotAvailableException.class,
                      TestSimpleServiceProvider.class })
public class ServiceTestSuite
{

}
