"""
This module defines types and functions related to a service lifecycle controller, 
which is used to manage the lifecycle of a single service instance
"""

from abc import ABC, abstractmethod
from service_instance_provider import ServiceInstanceProvider
from service_configuration import EMPTY_SERVICE_CONFIGURATION
from service import TService, CConfiguration, ServiceException


class ServiceLifecycleController[TService, CConfiguration](ABC):
    """
    ServiceLifecycleController is responsible for provide a control proxy to the Service.
    It's invoked by the ServiceManager to create, initialize, start, stop, and
    destroy a Service
    """

    @abstractmethod
    def init(
        self,
        service_instance_provider: ServiceInstanceProvider[TService],
        config: CConfiguration,
    ) -> None:
        """
        Register a service with the lifecycle controller.
        @param serviceInstanceProvider the factory that will create class instances of the service
               class
        @param config service level configuration
        """

    def shutdown(self) -> None:
        """
        Shutdown the service lifecycle and clean up
        """

    @abstractmethod
    def get_service(self) -> TService:
        """
        Retrieve a service instance

        @return a service instance
        """


class SingletonServiceLifecycleControllerImpl[TService, CConfiguration](
    ServiceLifecycleController[TService, CConfiguration]
):
    """
    Lifecycle controller that creates singletons
    """

    def init(
        self,
        service_instance_provider: ServiceInstanceProvider[TService],
        config: CConfiguration,
    ) -> None:
        self._service_instance_provider = service_instance_provider
        self._config = config

    def get_service(self) -> TService:
        if (not hasattr(self, "_singletonInstance")) or (
            self._singletoneInstance is None
        ):
            if (not hasattr(self, "_serviceInstanceProvider")) or (
                self._service_instance_provider is None
            ):
                raise ServiceException(
                    "Unexpected state. ServiceInstanceProvider not set"
                )

            self._singleton_instance = (
                self._service_instance_provider.createServiceInstance()
            )
            self._singleton_instance.init(self._config)
            self._singleton_instance.start()

        return self._singleton_instance

    def shutdown(self) -> None:
        if hasattr(self, "_singletonInstance") and (
            self._singletoneInstance is not None
        ):
            self._singleton_instance.stop()
            self._singleton_instance.destroy()
            self._singleton_instance = None

        self._config = EMPTY_SERVICE_CONFIGURATION
        self._service_instance_provider = None


class KnownServiceLifecycleControllers:
    """
    Known ServiceLifecycleController implementations.
    """

    SINGLETON: type = SingletonServiceLifecycleControllerImpl
