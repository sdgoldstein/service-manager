from typing import TypeVar
from abc import ABC, abstractmethod
from service_configuration import ServiceConfiguration

CConfiguration = TypeVar("CConfiguration", bound="ServiceConfiguration")
TService = TypeVar("TService", bound="Service")

class Service[CConfiguration](ABC):
    """A Service is a predefined component of functionality with a lifecycle managed by the ServiceManger."""
    @abstractmethod
    def init(self, configuration: C) -> None:
        """Initialize the service instance

        Args:
            configuration (C): Service level configuration"""
        pass

    @abstractmethod
    def start(self) -> None:
        """Start the service instances"""
        pass

    @abstractmethod
    def stop(self) -> None:
        """Stop the service instance"""
        pass

    @abstractmethod
    def destroy(self) -> None:
        """Destroy the service instance.  Can be used to clean up memory or other resources"""
        pass


class BaseService[CConfiguration](Service):
    """Base instance of a Service which is a noop for all methods.  Extend to create a new service and implement the methods that are required"""

    def init(self, configuration: C) -> None:
        pass

    def start(self) -> None:
        pass

    def stop(self) -> None:
        pass

    def destroy(self) -> None:
        pass

class ServiceException(Exception):
    """
    Service API Exception
    """
    pass
