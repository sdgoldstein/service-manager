from typing import TypeVar, Mapping
from abc import ABC, abstractmethod

ConfigValue = TypeVar("ConfigValue", int, float, str, bool)


class ServiceConfiguration(ABC):
    """A configuration for a service.  Provides a named set of properties"""

    @property
    @abstractmethod
    def popertyMap(self) -> Mapping[str, ConfigValue]:
        pass


class DefaultServiceConfigurationImpl(ServiceConfiguration):
    """
    A default service configuration implementation that accepts a map of
    properties in its constructor
    """

    def __init__(self, properties: Mapping[str, ConfigValue]):
        self._propertyMap: Mapping[str, ConfigValue] = properties

    @property
    def popertyMap(self) -> Mapping[str, ConfigValue]:
        return self._propertyMap


"""
  An empty service configuration available for convenience
 """
EMPTY_SERVICE_CONFIGURATION: ServiceConfiguration = DefaultServiceConfigurationImpl({})
