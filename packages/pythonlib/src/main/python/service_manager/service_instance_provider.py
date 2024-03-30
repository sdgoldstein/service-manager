from service import Service, TService

from typing import TypeVar, Mapping
from abc import ABC, abstractmethod


class ServiceInstanceProvider[TService](ABC):
    """
    A ServiceInstanceProvider creates a single instance of a service.
    """

    @abstractmethod
    def create_service_instance(self)->TService:
        """
        Create a Service instance
        """
        pass



class DefaultServiceInstanceProviderImpl[TService](ServiceInstanceProvider[TService]):
    """
    DefaultServiceInstanceProviderImpl is a service provider that simply invokes the provided service constructor to
    create the service instance
    """

    def __init__(self, serviceClass: type):
        self.serviceClass = serviceClass

    def create_service_instance(self)->TService:
        return self.serviceClass()

