from service import TService, CConfiguration, ServiceError
from service_instance_provider import ServiceInstanceProvider
from service_configuration import EMPTY_SERVICE_CONFIGURATION
from abc import ABC, abstractmethod

class ServiceLifecycleController[TService, CConfiguration](ABC):
    """
    ServiceLifecycleController is responsible for provide a control proxy to the Service.
    It's invoked by the ServiceManager to create, initialize, start, stop, and
    destroy a Service
    """

    @abstractmethod
    def init(self, serviceInstanceProvider: ServiceInstanceProvider[TService], config: CConfiguration)->None:
        """
        Register a service with the lifecycle controller.
        @param serviceInstanceProvider the factory that will create class instances of the service class
        @param config service level configuration
        """
        pass

    def shutdown(self)->None:
        """
        Shutdown the service lifecycle and clean up
        """
        pass

    @abstractmethod
    def getService(self) -> TService:
        """
        Retrieve a service instance
     
        @return a service instance
        """
        pass


class SingletonServiceLifecycleControllerImpl[TService, CConfiguration](ServiceLifecycleController[TService, CConfiguration]):
    """
    Lifecycle controller that creates singletons
    """

    def init(self, serviceInstanceProvider: ServiceInstanceProvider[TService], config: CConfiguration)->None:
        self._serviceInstanceProvider = serviceInstanceProvider;
        self._config = config;

    def getService(self) -> TService:
        if (not hasattr(self, "_singletonInstance")) or (self._singletoneInstance is None):
            if (not hasattr(self, "_serviceInstanceProvider")) or (self._serviceInstanceProvider is None):
                raise ServiceError("Unexpected state. ServiceInstanceProvider not set");
        
            self._singletonInstance = self._serviceInstanceProvider.createServiceInstance();
            self._singletonInstance.init(self._config);
            self._singletonInstance.start();

        return self._singletonInstance;

    def shutdown(self)->None:
        if hasattr(self, "_singletonInstance") and (self._singletoneInstance is not None):
            self._singletonInstance.stop();
            self._singletonInstance.destroy();
            self._singletonInstance = None;
        
        self._config = EMPTY_SERVICE_CONFIGURATION;
        self._serviceInstanceProvider = None;

class KnownServiceLifecycleControllers:
    """
    Known ServiceLifecycleController implementations.  
    """

    SINGLETON:type = SingletonServiceLifecycleControllerImpl


