package com.sphyrna.servicemanager;

public class ServiceManagerConstants
{
    public static final ServiceConfiguration EMPTY_SERVICE_CONFIGURATION = new ServiceConfiguration() {
        @Override
        public Object getProperty(String name)
        {
            throw new IllegalArgumentException("Empty congiruration does not have any properties");
        }

        @Override
        public boolean containsProperty(String name)
        {
            return false;
        }
    };
}
