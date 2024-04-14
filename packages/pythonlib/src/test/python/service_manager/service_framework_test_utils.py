from service_instance_provider import ServiceInstanceProvider
from service_lifecycle_controller import ServiceLifecycleController

from service import BaseService, CConfiguration, TService


class MockServiceInstanceProvider(ServiceInstanceProvider[TService]):
    def __init__(self, mock_service: TService):
        self.created_times = 0
        self.mock_service = mock_service

    def create_service_instance(self) -> TService:
        self.created_times += 1
        return self.mock_service


class MockService(BaseService):
    pass


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
        pass

    def shutdown(self) -> None:
        pass

    def get_service(self) -> TService:
        pass
