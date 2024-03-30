"""
This module defines types and functions related to a service manager strategy, 
which is used to specify a concrete service manager strategy
"""

from abc import ABC, abstractmethod
from typing import Dict
from service_configuration import ServiceConfiguration, EMPTY_SERVICE_CONFIGURATION
from service_lifecycle_controller import (
    KnownServiceLifecycleControllers,
    ServiceLifecycleController,
)
from service_instance_provider import (
    ServiceInstanceProvider,
    DefaultServiceInstanceProviderImpl,
)
from service import ServiceException, TService, CConfiguration


class ServiceManagerStrategy(ABC):
    """
    Defines a Service Manager Strategy that controls the behavior of the ServiceManager
    """

    @abstractmethod
    def get_service[
        TService, CConfiguration
    ](
        self, name: str, config: CConfiguration = EMPTY_SERVICE_CONFIGURATION
    ) -> TService:
        """
        Retrieve a service.

        An Optional config may be supplied for service instance level
        configuration.  self must be supported by the associated
        ServiceLifecycleManager
        """

    @abstractmethod
    def is_service_defined(self, name: str) -> bool:
        """
        Determine if a service with the specified name has been defined
        """

    @abstractmethod
    def shutdown(self) -> None:
        """
        Shutdown the ServiceManager and all associated services
        """


class ServiceDefinition[TService]:
    """
    Internal class
    """

    def __init__(
        self,
        service_lifecycle_controller: ServiceLifecycleController[TService],
        service_instance_provider: ServiceInstanceProvider[
            TService
        ] = DefaultServiceInstanceProviderImpl[TService](),
        config: ServiceConfiguration = EMPTY_SERVICE_CONFIGURATION,
    ):
        self.service_lifecycle_controller = service_lifecycle_controller
        self._service_instance_provider = service_instance_provider
        self.service_configuration = config

    def has_service_instance_provider(self) -> bool:
        """
        Check if the instance provider has been defined for this ServiceDefinition
        FIXME - is the service instance provider always defined?
        """
        return (hasattr(self, "_serviceInstanceProvider")) and (
            self._service_instance_provider is not None
        )

    def get_service_instance_provider(self) -> ServiceInstanceProvider[TService]:
        """
        Retrieve the service instance provider for this ServiceDefinition
        FIXME - Check Python Property support
        """
        if not self.has_service_instance_provider():
            raise ServiceException(
                "get serviceInstanceProvider called when one doesn't exist"
            )

        return self._service_instance_provider


class DefaultServiceManagerStrategyImpl(ServiceManagerStrategy):
    """
    This is the default service manager strategy.
    It's used by clients to programmetically register/define services
    """

    def __init__(self):
        self._controllers_for_active_services = Dict[
            str, ServiceLifecycleController[TService]
        ]()
        self._service_definitions = Dict[str, ServiceDefinition[TService]]

    def register_service_by_controller_only[
        TService, CConfiguration
    ](
        self,
        name: str,
        service_lifecycle_controller: ServiceLifecycleController[
            TService, CConfiguration
        ],
        override: bool = False,
    ) -> None:
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
            self._handle_service_override(name, override)

        service_definition: ServiceDefinition[TService] = ServiceDefinition(
            service_lifecycle_controller
        )
        self._service_definitions[name] = service_definition

    def register_service[
        TService, CConfiguration
    ](
        self,
        name: str,
        service_instance_provider: ServiceInstanceProvider[TService],
        service_lifecycle_controller: ServiceLifecycleController[
            TService, CConfiguration
        ],
        config: CConfiguration = EMPTY_SERVICE_CONFIGURATION,
        override: bool = False,
    ) -> bool:
        if name in self._service_definitions:
            self._handle_service_override(name, override)

        service_definition: ServiceDefinition[TService] = ServiceDefinition(
            service_lifecycle_controller, service_instance_provider, config
        )
        self._service_definitions[name] = service_definition

    # FIXME - type as it's used here isn't strong enough.
    def register_service_by_class[
        TService, CConfiguration
    ](
        self,
        name: str,
        service_class: type,
        service_lifecycle_controller_class: type,
        config: CConfiguration = EMPTY_SERVICE_CONFIGURATION,
        override: bool = False,
    ) -> None:
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

        service_provider = DefaultServiceInstanceProviderImpl(service_class)
        service_lifecycle_controller = service_lifecycle_controller_class()
        self.register_service(
            name, service_provider, service_lifecycle_controller, config, override
        )

    def register_singleton_service[
        TService, CConfiguration
    ](
        self,
        name: str,
        service_class: type,
        config: CConfiguration = EMPTY_SERVICE_CONFIGURATION,
        override: bool = False,
    ) -> None:
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
        self.register_service_by_class(
            name,
            service_class,
            KnownServiceLifecycleControllers.SINGLETON,
            config,
            override,
        )

    def get_service[
        TService, CConfiguration
    ](
        self, name: str, config: CConfiguration = EMPTY_SERVICE_CONFIGURATION
    ) -> TService:

        service_lifecycle_controller: ServiceLifecycleController[TService]

        if name not in self._service_definitions:
            raise ServiceException("Service not defined: " + name)

        # Check if the service lifecycle controller has already been initialized
        if name in self._controllers_for_active_services:
            service_lifecycle_controller = self._controllers_for_active_services[name]
        else:
            service_definition: ServiceDefinition[TService] = self._service_definitions[
                name
            ]
            service_lifecycle_controller = (
                service_definition.service_lifecycle_controller
            )

            if service_definition.has_service_instance_provider():
                service_lifecycle_controller.init(
                    service_definition.serviceInstanceProvider,
                    service_definition.service_configuration,
                )

            self._controllers_for_active_services[name] = service_lifecycle_controller

        return service_lifecycle_controller.get_service()

    def is_service_defined(self, name: str) -> bool:
        return name in self._service_definitions

    def shutdown(self) -> None:
        for next_service_controller in self._controllers_for_active_services:
            next_service_controller.shutdown()

        self._controllers_for_active_services.clear()
        self._service_definitions.clear()

    def _handle_service_override(self, name: str, override: bool) -> None:
        if override:
            # Remove defintions.  Shutdown lifecycle controller
            del self._service_definitions[name]
            if name in self._controllers_for_active_services:
                controller_to_remove = self._controllers_for_active_services[name]
                controller_to_remove.shutdown()
                del self._controllers_for_active_services[name]
        else:
            raise ServiceException(
                "Service with name, :"
                + name
                + ", is already defined.  Specify override=true if it should be replaced"
            )


class ConfigServiceManagerStrategy(DefaultServiceManagerStrategyImpl):
    """
    A strategy that reads service configuration from serviceConfig.json.  FIXME - To Be Implemented
    """
