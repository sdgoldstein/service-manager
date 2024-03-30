from abc import ABC, abstractmethod
from service_configuration import EMPTY_SERVICE_CONFIGURATION
from service_manager_strategy import ServiceManagerStrategy, ConfigServiceManagerStrategy
from service import TService, CConfiguration


class ServiceManager:
    """
        The ServiceManager is used to manage and retrieve services.  It's a proxy to
        a ServiceManagerStrategy implementation, which is used to modify the
        ServiceManager behavior.  The static proxy simplifies client code
    """

    _serviceManagerStrategy: ServiceManagerStrategy = ConfigServiceManagerStrategy();

    @classmethod
    def getService[TService, CConfiguration](name: string, config: CConfiguraiton=EMPTY_SERVICE_CONFIGURATION)-> TService:
        """
            Retrieve a service.
     
            An optional config may be supplied for service instance level
            configuration.  This must be supported by the associated
            ServiceLifecycleManager
        """
        return ServiceManager._serviceManagerStrategy.getService[T](name, config)
 
    @classmethod
    def isServiceDefined(name: str)-> bool:
        """
        Determine if a service with the specified name has been defined
        """
        return ServiceManager._serviceManagerStrategy.isServiceDefined(name);
    
    @classmethod
    def shutdown()->None:
        """
            Shutdown the ServiceManager and all associated services
        """
        ServiceManager._serviceManagerStrategy.shutdown();

    @classmethod
    def setDefaultStrategy(strategyToSet: ServiceManagerStrategy)->None:
        """
        Set the service manager strategy.  This strategy will be used to retrieve services based on the algorithm defined
        in the ServiceManagerFactory implementnatation
        
        In the future, a ServiceManager may be able to use multiple strategies concurrently.  As of the current API, it
        only supports a single strategy
        
        @param strategyToSet the service manager strategy to set
        """
        ServiceManager._serviceManagerStrategy = strategyToSet;



