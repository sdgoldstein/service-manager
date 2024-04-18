import pytest

from sphyrna.service_manager.service import BaseService
from sphyrna.service_manager.service_instance_provider import (
    DefaultServiceInstanceProviderImpl,
)


class MockService(BaseService):
    def __init__(self):
        self.was_called = True


def test_constructor_and_create_service_instance():
    # Create an instance of DefaultServiceInstanceProviderImpl with TestService as a parameter
    service_instance_provider = DefaultServiceInstanceProviderImpl(MockService)

    # Call createServiceInstance on the DefaultServiceInstanceProviderImpl instance
    service_instance = service_instance_provider.create_service_instance()

    # Assert that the constructor of TestService was called
    assert service_instance.was_called

    # Assert that the service instance is an instance of TestService
    assert isinstance(service_instance, MockService)
