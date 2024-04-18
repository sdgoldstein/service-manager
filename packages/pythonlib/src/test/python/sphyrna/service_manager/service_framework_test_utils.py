"""
    Utility classes and functions used during testing
"""

from sphyrna.service_manager.service import BaseService, ConfigurationT, ServiceT
from sphyrna.service_manager.service_instance_provider import ServiceInstanceProvider
from sphyrna.service_manager.service_lifecycle_controller import (
    ServiceLifecycleController,
)


class MockServiceInstanceProvider(ServiceInstanceProvider[ServiceT]):
    """
    Mock Service Instance Provider
    """

    def __init__(self, mock_service: ServiceT):
        self.created_times = 0
        self.mock_service = mock_service

    def create_service_instance(self) -> ServiceT:
        self.created_times += 1
        return self.mock_service


class MockService(BaseService):
    """
    Mock Service
    """

    pass


class MockServiceLifecycleController(
    ServiceLifecycleController[ServiceT, ConfigurationT]
):
    """
    Mock Service Lifecycle Manager
    """

    def __init__(self):
        # Placeholder for mocking
        pass

    def init(
        self,
        service_instance_provider: ServiceInstanceProvider[ServiceT],
        config: ConfigurationT,
    ) -> None:
        pass

    def shutdown(self) -> None:
        pass

    def get_service(self) -> ServiceT:
        pass
