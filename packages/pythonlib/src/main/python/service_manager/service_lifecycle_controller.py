"""
This module defines types and functions related to a service lifecycle controller, 
which is used to manage the lifecycle of a single service instance
"""

from abc import ABC, abstractmethod
from typing import ClassVar
from typing_extensions import override
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


# pylint isn't handling generics in the way I'm specifing them
# pylint: disable=E1136,unused-variable
class SingletonServiceLifecycleControllerImpl[TService, CConfiguration](
    ServiceLifecycleController[TService, CConfiguration]
):
    """
    Lifecycle controller that creates singletons
    """

    def __init__(self):
        self._service_instance_provider = None
        self._config = EMPTY_SERVICE_CONFIGURATION
        self._singleton_instance = None

    @override
    def init(
        self,
        service_instance_provider: ServiceInstanceProvider[TService],
        config: CConfiguration,
    ) -> None:
        # Docstring on parent
        # pylint: disable=missing-function-docstring

        self._service_instance_provider = service_instance_provider
        self._config = config

    @override
    def get_service(self) -> TService:
        # Docstring on parent
        # pylint: disable=missing-function-docstring

        if (not hasattr(self, "_singleton_instance")) or (
            self._singleton_instance is None
        ):
            if (not hasattr(self, "_service_instance_provider")) or (
                self._service_instance_provider is None
            ):
                raise ServiceException(
                    "Unexpected state. ServiceInstanceProvider not set"
                )

            self._singleton_instance = (
                self._service_instance_provider.create_service_instance()
            )
            self._singleton_instance.init(self._config)
            self._singleton_instance.start()

        return self._singleton_instance

    @override
    def shutdown(self) -> None:
        # Docstring on parent
        # pylint: disable=missing-function-docstring

        if hasattr(self, "_singleton_instance") and (
            self._singleton_instance is not None
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

    SINGLETON: ClassVar[type] = SingletonServiceLifecycleControllerImpl
