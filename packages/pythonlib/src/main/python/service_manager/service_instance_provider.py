"""
This module defines types and functions related to a service instance provider, 
which is used to create an instance of a service class
"""

from abc import ABC, abstractmethod
from service import TService


class ServiceInstanceProvider[TService](ABC):
    """
    A ServiceInstanceProvider creates a single instance of a service.
    """

    @abstractmethod
    def create_service_instance(self) -> TService:
        """
        Create a Service instance
        """


class DefaultServiceInstanceProviderImpl[TService](ServiceInstanceProvider[TService]):
    """
    DefaultServiceInstanceProviderImpl is a service provider that simply invokes the provided service constructor to
    create the service instance
    """

    def __init__(self, service_class: type):
        self.service_class = service_class

    def create_service_instance(self) -> TService:
        """
        Create an instance of the service class
        """
        return self.service_class()
