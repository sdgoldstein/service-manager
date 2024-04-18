"""
    Test thje service_lifecycle_controller module
"""

from unittest.mock import Mock
import pytest

from service_framework_test_utils import MockServiceInstanceProvider
from sphyrna.service_manager.service import BaseService, ServiceException
from sphyrna.service_manager.service_configuration import (
    DefaultServiceConfigurationImpl,
)
from sphyrna.service_manager.service_lifecycle_controller import (
    SingletonServiceLifecycleControllerImpl,
)


class TestSingletonServiceLifecycleControllerImpl:
    """
    Test SingonServiceLifecycleControllerImpl
    """

    @classmethod
    def setup_class(cls):
        """
        Set code run once before any test methods in the class
        """
        cls.mock_service = Mock(BaseService)
        cls.test_service_configuration = DefaultServiceConfigurationImpl(
            {"key1": "value1", "key2": "value2"}
        )

    def setup_method(self, method):
        """
        Setup code run before each test methods in the class
        """

        self._controller_to_test = SingletonServiceLifecycleControllerImpl()
        self.__class__.mock_service.reset_mock()
        self._mock_service_instanceProvider = MockServiceInstanceProvider(
            self.__class__.mock_service
        )

    def test_initialize_and_start(self):
        """
        Test the initialize and start methods
        """
        # First call getService without calling init
        with pytest.raises(ServiceException):
            self._controller_to_test.get_service()

        # Now, initialize and get service
        self._controller_to_test.init(
            self._mock_service_instanceProvider,
            self.__class__.test_service_configuration,
        )
        instance = self._controller_to_test.get_service()
        assert (
            instance == self.__class__.mock_service
        ), "service instance equals mock service"
        self.__class__.mock_service.init.assert_called_once_with(
            self.__class__.test_service_configuration
        )
        self.__class__.mock_service.start.assert_called_once()
        assert (
            self._mock_service_instanceProvider.created_times == 1
        ), "service instance created once"

        # Try to obtain it again
        self.__class__.mock_service.reset_mock()
        same_instance = self._controller_to_test.get_service()
        assert (
            same_instance == self.__class__.mock_service
        ), "same service instance equals mock service"
        self.__class__.mock_service.init.assert_not_called()
        self.__class__.mock_service.start.assert_not_called()
        assert (
            self._mock_service_instanceProvider.created_times == 1
        ), "service instance still created once"

    def test_shutdown_stop_destroy(self):
        """
        Test the shutdown stop and destroy methods
        """
        # Initialize and get the service started
        self._controller_to_test.init(
            self._mock_service_instanceProvider,
            self.__class__.test_service_configuration,
        )
        self._controller_to_test.get_service()

        # Now, shutdown
        self._controller_to_test.shutdown()
        self.__class__.mock_service.stop.assert_called_once()
        self.__class__.mock_service.destroy.assert_called_once()

        # Once it's shutdown, expect methods to throw errors
        with pytest.raises(ServiceException):
            self._controller_to_test.get_service()
