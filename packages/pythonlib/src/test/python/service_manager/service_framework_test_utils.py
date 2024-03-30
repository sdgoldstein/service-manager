from abc import ABC, abstractmethod
from typing import TypeVar

from service_instance_provider import ServiceInstanceProvider
from service import CConfiguration, TService
from service_lifecycle_controller import ServiceLifecycleController


class MockServiceInstanceProvider(ServiceInstanceProvider[TService]):
    def __init__(self, mock_service: TService):
        self.created_times = 0
        self.mock_service = mock_service

    def create_service_instance(self) -> TService:
        self.created_times += 1
        return self.mock_service


class MockServiceInstanceProvider(ServiceInstanceProvider[TService]):
    def __init__(self, mock_service: TService):
        self.created_times = 0
        self.mock_service = mock_service

    def create_service_instance(self) -> TService:
        self.created_times += 1
        return self.mock_service


class MockServiceLifecycleController(
    ServiceLifecycleController[TService, CConfiguration]
):
    def __init__(self):
        # Placeholder for mocking
        pass

    def init(
        self,
        service_instance_provider: ServiceInstanceProvider[TService],
        config: CConfiguration,
    ) -> None:
        raise NotImplementedError("Method not implemented.")

    def shutdown(self) -> None:
        raise NotImplementedError("Method not implemented.")

    def get_service(self) -> TService:
        raise NotImplementedError("Method not implemented.")
