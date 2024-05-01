package com.sphyrna.servicemanager.providers;

import com.sphyrna.servicemanager.ServiceLifecycleController;

/**
 * Known ServiceLifecycleController implementations.  To create an instance, use the new keyword with the constant
 * member variable
 *
 * For example, "new KnownServiceLifecycleControllers.SINGLETON()"
 */
public class KnownServiceLifecycleControllers
{
    public static final Class SINGLETON = SingletonServiceLifecycleController.class;
}
