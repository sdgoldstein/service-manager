from abc import ABC, abstractmethod
from typing import Dict
from service_configuration import EMPTY_SERVICE_CONFIGURATION
from service import ServiceError, TService, CConfiguration
from service_lifecycle_controller import KnownServiceLifecycleControllers, ServiceLifecycleController
from service_instance_provider import ServiceInstanceProvider, DefaultServiceInstanceProviderImpl
from service_configuration import ServiceConfiguration


class ServiceManagerStrategy(ABC):
    """
        Defines a Service Manager Strategy that controls the behavior of the ServiceManager
    """
    
    @abstractmethod
    def getService[TService, CConfiguration](self, name: str, config: CConfiguration=EMPTY_SERVICE_CONFIGURATION)->TService:
        """
        Retrieve a service.
        
        An Optional config may be supplied for service instance level
        configuration.  self must be supported by the associated
        ServiceLifecycleManager
        """
        pass

    @abstractmethod
    def isServiceDefined(self, name: str)->bool:
        """
        Determine if a service with the specified name has been defined
        """
        pass

    @abstractmethod
    def shutdown(self)->None:
        """
        Shutdown the ServiceManager and all associated services
        """
        pass


class ServiceDefinition[TService]
    """
    Internal class
    """

    def __init__(self, serviceLifecycleController: ServiceLifecycleController[TService], serviceInstanceProvider: ServiceInstanceProvider[TService]=DefaultServiceInstanceProviderImpl[TService](), config: ServiceConfiguration=EMPTY_SERVICE_CONFIGURATION):
        self._serviceInstanceProvider = serviceInstanceProvider;
        self.serviceConfiguration = config;

    def has_service_instance_provider(self) -> bool:
        return (hasattr(self, "_serviceInstanceProvider")) and (self._serviceInstanceProvider is not None)


    def get_service_instance_provider(self)-> ServiceInstanceProvider[TService]:
        if (not self.has_service_instance_provider()):
            raise ServiceError("get serviceInstanceProvider called when one doesn't exist")
        
        return self._serviceInstanceProvider;


class DefaultServiceManagerStrategyImpl(ServiceManagerStrategy):

    def __init__(self):
        self._controllers_for_active_services = Dict[str, ServiceLifecycleController[TService]]()
        self._service_defintions = Dict[str, ServiceDefinition[TService]]

    def registerServiceByControllerOnly[TService, CConfiguration](self, name: str, serviceLifecycleController: ServiceLifecycleController[TService, CConfiguration], override: bool=False)->None:
        """
            Register a service.  self is the minimal number of inputs needed to register a new service.
     
            IMPORTANT NOTE: If registering a lifecycle controller through this method, the init(serviceInstanceProvider:
            ServiceInstanceProvider[T], config: C) method will NOT be called.  If it must be called, it should be invoked by
            the client before registering the service
            
            @param name
                        the name of the defined service
            @param serviceLifecycleController
                        controls the service lifeycle. For known controllers,
            see {@KnownServiceLifecycleControllers}
            @param override
                        An optional bool indicating whether or not to override an
            existing defintion with the same name. If self is false and a service
            definition already exists, an error will be thrown
     """
        if name in self._service_definitions:
            self._handle_service_override(name, override);

        service_definition: ServiceDefinition[TService] = ServiceDefinition(serviceLifecycleController);
        self._service_definitions[name] = service_definition;

    def registerService[TService, CConfiguration](self, name: str, serviceInstanceProvider: ServiceInstanceProvider[TService], serviceLifecycleController: ServiceLifecycleController[TService, CConfiguration], config: CConfiguration=EMPTY_SERVICE_CONFIGURATION, override: bool=False) -> bool:
        if name in self._service_definitions:
            self._handle_service_override(name, override);

        service_definition: ServiceDefinition[TService] = ServiceDefinition(serviceLifecycleController, serviceInstanceProvider, config);
        self._service_definitions[name] = service_definition

# FIXME - type as it's used here isn't strong enough. 
    def registerServiceByClass[TService, CConfiguration](self, name: str, service_class: type, serviceLifecycleControllerClass: type, config: CConfiguration=EMPTY_SERVICE_CONFIGURATION, override: bool=False)->None:
        """
        Register a service providing a contructor method for a service class and service lifecycle controller class
        
        @param name
                    the name of the defined service
        @param serviceClass
                    the class that implements the service
        @param serviceLifecycleControllerClass
                    the class that controls the service lifeycle. For known controllers,
        see {@KnownServiceLifecycleControllers}
        @param config
                    An Optional config may be supplied for service definition level
        configuration.
        @param override
                    An optional bool indicating whether or not to override an
        existing defintion with the same name. If self is false and a service
        definition already exists, an error will be thrown
        """

        service_provider = DefaultServiceInstanceProviderImpl(service_class);
        serviceLifecycleController = serviceLifecycleControllerClass();
        self.registerService(name, service_provider, serviceLifecycleController, config, override);

    def registerSingletonService[TService, CConfiguration](self, name: str, serviceClass: type, config: CConfiguration=EMPTY_SERVICE_CONFIGURATION, override: bool=False)->None:
        """
        Register a service that has a singleton lifecycle (only one instance exists)
     
        @param name
                    the name of the defined service
        @param serviceClass
                    the class that implements the service
        @param config
                    An Optional config may be supplied for service definition level
        configuration.
        @param override
                    An optional bool indicating whether or not to override an
        existing defintion with the same name. If self is false and a service
        definition already exists, an error will be thrown
        """
        self.registerServiceByClass(name, serviceClass, KnownServiceLifecycleControllers.SINGLETON, config, override);
    
    def getService[TService, CConfiguration](self, name: str, config: CConfiguration=EMPTY_SERVICE_CONFIGURATION)->TService:

        serviceLifecycleController: ServiceLifecycleController[TService];

        if name not in self._service_definitions:
            raise ServiceError("Service not defined: " + name);
        
        # Check if the service lifecycle controller has already been initialized
        if name in self.controllers_for_active_services:
            serviceLifecycleController = self.controllers_for_active_services[name]                                                                              
        else:
            serviceDefinition: ServiceDefinition[TService] = self._service_definitions[name];
            serviceLifecycleController = serviceDefinition.serviceLifecycleController;

            if serviceDefinition.has_service_instance_provider():
                serviceLifecycleController.init(serviceDefinition.serviceInstanceProvider,
                                                serviceDefinition.serviceConfiguration)

            self.controllers_for_active_services[name] = serviceLifecycleController


        return serviceLifecycleController.getService()

    def isServiceDefined(self, name: str) -> bool:
        return name in self._service_definitions

    def shutdown(self)->void:
        for nextServiceController in self._controllers_for_active_services:
            nextServiceController.shutdown()

        self.controllers_for_active_services.clear();
        self._service_definitions.clear();

    def _handleServiceOverride(self, name: str, override: bool)->None:
        if override:
            # Remove defintions.  Shutdown lifecycle controller
            del self._service_definitions[name]
            if name in self.controllers_for_active_services:
                controllerToRemove = self.controllers_for_active_services[name]
                controllerToRemove.shutdown();
                del self.controllers_for_active_services[name]                                                 
        else:
            raise ServiceError("Service with name, :" + name + ", is already defined.  Specify override=true if it should be replaced")


class ConfigServiceManagerStrategy(DefaultServiceManagerStrategyImpl):
    """
    A strategy that reads service configuration from serviceConfig.json.  FIXME - To Be Implemented
    """

