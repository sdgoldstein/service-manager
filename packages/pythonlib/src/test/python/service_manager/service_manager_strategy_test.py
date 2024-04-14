"""
    Test the service_manager_strategy module
"""

from unittest.mock import Mock
import pytest
from service_configuration import (
    EMPTY_SERVICE_CONFIGURATION,
    DefaultServiceConfigurationImpl,
)
from service_framework_test_utils import (
    MockService,
    MockServiceInstanceProvider,
    MockServiceLifecycleController,
)
from service_lifecycle_controller import ServiceLifecycleController
from service_manager_strategy import DefaultServiceManagerStrategyImpl

from service import BaseService, ServiceException


class TestDefaultServiceManagerStrategyImpl:
    """
    Test DefaultServiceManagerStrategyImpl
    """

    @classmethod
    def setup_class(cls):
        """
        Set code run once before any test methods in the class
        """
        cls.mock_service = Mock(BaseService)
        wrapped_service_instance_provider = MockServiceInstanceProvider(
            cls.mock_service
        )
        cls.mock_service_instance_provider = Mock(
            wrapped_service_instance_provider, wraps=wrapped_service_instance_provider
        )
        cls.test_service_configuration = DefaultServiceConfigurationImpl(
            {"key1": "value1", "key2": "value2"}
        )
        cls.mock_service_lifecycle_controller = Mock(ServiceLifecycleController)

    def setup_method(self, method):
        """
        Setup code run before each test methods in the class
        """

        self.strategy_to_test = DefaultServiceManagerStrategyImpl()
        self.__class__.mock_service.reset_mock()
        self.__class__.mock_service_instance_provider.reset_mock()
        self.__class__.mock_service_lifecycle_controller.reset_mock()

    def test_register_service_is_service_defined_get_service(self):
        """
        test register_service/is_service_defined/get_service
        """

        service_name_one = "service_name_one"
        assert (
            self.strategy_to_test.is_service_defined(service_name_one) is False
        ), "service_name_one initially undefined"
        with pytest.raises(ServiceException):
            self.strategy_to_test.get_service(service_name_one)

        # register a service
        self.strategy_to_test.register_service(
            service_name_one,
            self.__class__.mock_service_instance_provider,
            self.__class__.mock_service_lifecycle_controller,
        )
        assert (
            self.strategy_to_test.is_service_defined(service_name_one) is True
        ), "service_name_one defined after registration"
        self.strategy_to_test.get_service(service_name_one)
        self.__class__.mock_service_lifecycle_controller.init.assert_called_once_with(
            self.__class__.mock_service_instance_provider, EMPTY_SERVICE_CONFIGURATION
        )
        self.__class__.mock_service_lifecycle_controller.get_service.assert_called_once()

        # register a service with config
        self.__class__.mock_service_lifecycle_controller.reset_mock()
        service_name_two = "service_name_two"
        self.strategy_to_test.register_service(
            service_name_two,
            self.__class__.mock_service_instance_provider,
            self.__class__.mock_service_lifecycle_controller,
            self.__class__.test_service_configuration,
        )
        self.strategy_to_test.get_service(service_name_two)
        self.__class__.mock_service_lifecycle_controller.init.assert_called_once_with(
            self.__class__.mock_service_instance_provider,
            self.__class__.test_service_configuration,
        )

        # Override a service
        self.__class__.mock_service_lifecycle_controller.reset_mock()
        self.strategy_to_test.register_service(
            service_name_one,
            self.__class__.mock_service_instance_provider,
            self.__class__.mock_service_lifecycle_controller,
            self.__class__.test_service_configuration,
            True,
        )
        assert (
            self.strategy_to_test.is_service_defined(service_name_one) is True
        ), "service_name_one defined after overriden"
        self.strategy_to_test.get_service(service_name_one)
        self.__class__.mock_service_lifecycle_controller.init.assert_called_once_with(
            self.__class__.mock_service_instance_provider,
            self.__class__.test_service_configuration,
        )
        self.__class__.mock_service_lifecycle_controller.get_service.assert_called_once()

        # test overriding with registering with service with controller only
        self.__class__.mock_service_lifecycle_controller.reset_mock()
        self.strategy_to_test.register_service_by_controller_only(
            service_name_two, self.__class__.mock_service_lifecycle_controller, True
        )
        assert (
            self.strategy_to_test.is_service_defined(service_name_two) is True
        ), "service_name_one defined after overriden by controller only"
        self.strategy_to_test.get_service(service_name_two)
        self.__class__.mock_service_lifecycle_controller.init.assert_not_called()
        self.__class__.mock_service_lifecycle_controller.get_service.assert_called_once()

        # test service error for False override
        with pytest.raises(ServiceException):
            self.strategy_to_test.register_service(
                service_name_one,
                self.__class__.mock_service_instance_provider,
                self.__class__.mock_service_lifecycle_controller,
                self.__class__.test_service_configuration,
                False,
            )

        with pytest.raises(ServiceException):
            self.strategy_to_test.register_service(
                service_name_one,
                self.__class__.mock_service_instance_provider,
                self.__class__.mock_service_lifecycle_controller,
                self.__class__.test_service_configuration,
            )

        with pytest.raises(ServiceException):
            self.strategy_to_test.register_service(
                service_name_two,
                self.__class__.mock_service_instance_provider,
                self.__class__.mock_service_lifecycle_controller,
                self.__class__.test_service_configuration,
            )

    def test_register_service_by_controller_only_is_service_defined_get_service(self):
        """
        test register_service_by_controller_only/is_service_defined/get_service
        """

        service_name_one = "service_name_one"
        assert (
            self.strategy_to_test.is_service_defined(service_name_one) is False
        ), "service_name_one initially undefined"
        with pytest.raises(ServiceException):
            self.strategy_to_test.get_service(service_name_one)

        # register a service
        self.strategy_to_test.register_service_by_controller_only(
            service_name_one, self.__class__.mock_service_lifecycle_controller
        )
        assert (
            self.strategy_to_test.is_service_defined(service_name_one) is True
        ), "service_name_one defined after registration"
        self.strategy_to_test.get_service(service_name_one)
        self.__class__.mock_service_lifecycle_controller.init.assert_not_called()
        self.__class__.mock_service_lifecycle_controller.get_service.assert_called_once()

        # Override a service
        self.__class__.mock_service_lifecycle_controller.reset_mock()
        self.strategy_to_test.register_service_by_controller_only(
            service_name_one, self.__class__.mock_service_lifecycle_controller, True
        )
        assert (
            self.strategy_to_test.is_service_defined(service_name_one) is True
        ), "service_name_one defined after overriden"
        self.strategy_to_test.get_service(service_name_one)
        self.__class__.mock_service_lifecycle_controller.init.assert_not_called()
        self.__class__.mock_service_lifecycle_controller.get_service.assert_called_once()

        # test overriding with registering with service with provider and config
        self.__class__.mock_service_lifecycle_controller.reset_mock()
        service_name_two = "service_name_two"
        self.strategy_to_test.register_service_by_controller_only(
            service_name_two, self.__class__.mock_service_lifecycle_controller
        )
        assert (
            self.strategy_to_test.is_service_defined(service_name_two) is True
        ), "service_name_two defined after overriden by controller only"
        self.strategy_to_test.register_service(
            service_name_two,
            self.__class__.mock_service_instance_provider,
            self.__class__.mock_service_lifecycle_controller,
            self.__class__.test_service_configuration,
            True,
        )
        assert (
            self.strategy_to_test.is_service_defined(service_name_two) is True
        ), "service_name_two defined after overriden"

        self.strategy_to_test.get_service(service_name_two)
        self.__class__.mock_service_lifecycle_controller.init.assert_called_once_with(
            self.__class__.mock_service_instance_provider,
            self.__class__.test_service_configuration,
        )
        self.__class__.mock_service_lifecycle_controller.get_service.assert_called_once()

        # test service error for False override
        with pytest.raises(ServiceException):
            self.strategy_to_test.register_service_by_controller_only(
                service_name_one,
                self.__class__.mock_service_lifecycle_controller,
                False,
            )
        with pytest.raises(ServiceException):
            self.strategy_to_test.register_service_by_controller_only(
                service_name_one, self.__class__.mock_service_lifecycle_controller
            )
        with pytest.raises(ServiceException):
            self.strategy_to_test.register_service_by_controller_only(
                service_name_two, self.__class__.mock_service_lifecycle_controller
            )

    def test_register_service_by_class_is_service_defined_get_service(self):
        """
        test register_service_by_class/is_service_defined/get_service
        """

        service_name_one = "service_name_one"
        assert (
            self.strategy_to_test.is_service_defined(service_name_one) is False
        ), "service_name_one initially undefined"
        with pytest.raises(ServiceException):
            self.strategy_to_test.get_service(service_name_one)

        # register a service
        self.strategy_to_test.register_service_by_class(
            service_name_one, MockService, MockServiceLifecycleController
        )
        assert (
            self.strategy_to_test.is_service_defined(service_name_one) is True
        ), "service_name_one defined after registration by class"
        self.strategy_to_test.get_service(service_name_one)

        # register a service with config
        self.__class__.mock_service_lifecycle_controller.reset_mock()
        service_name_two = "service_name_two"
        self.strategy_to_test.register_service_by_class(
            service_name_two,
            MockService,
            MockServiceLifecycleController,
            self.__class__.test_service_configuration,
        )
        assert (
            self.strategy_to_test.is_service_defined(service_name_two) is True
        ), "service_name_two defined after registration by class"
        self.strategy_to_test.get_service(service_name_two)

        # Override a service
        self.__class__.mock_service_lifecycle_controller.reset_mock()
        self.strategy_to_test.register_service_by_class(
            service_name_one,
            MockService,
            MockServiceLifecycleController,
            self.__class__.test_service_configuration,
            True,
        )
        assert (
            self.strategy_to_test.is_service_defined(service_name_one) is True
        ), "service_name_one defined after registration by class with override"
        self.strategy_to_test.get_service(service_name_one)

        # test overriding with registering with service with controller only
        self.__class__.mock_service_lifecycle_controller.reset_mock()
        self.strategy_to_test.register_service_by_controller_only(
            service_name_two, self.__class__.mock_service_lifecycle_controller, True
        )
        assert (
            self.strategy_to_test.is_service_defined(service_name_two) is True
        ), "service_name_two defined after registration by class with override by controller only"
        self.strategy_to_test.get_service(service_name_two)
        self.__class__.mock_service_lifecycle_controller.init.assert_not_called()
        self.__class__.mock_service_lifecycle_controller.get_service.assert_called_once()

        # test service error for False override
        with pytest.raises(ServiceException):
            self.strategy_to_test.register_service_by_class(
                service_name_one,
                MockService,
                MockServiceLifecycleController,
                self.__class__.test_service_configuration,
                False,
            )

        with pytest.raises(ServiceException):
            self.strategy_to_test.register_service_by_class(
                service_name_one,
                MockService,
                MockServiceLifecycleController,
                self.__class__.test_service_configuration,
            )

        with pytest.raises(ServiceException):
            self.strategy_to_test.register_service_by_class(
                service_name_two,
                MockService,
                MockServiceLifecycleController,
                self.__class__.test_service_configuration,
            )

    def test_register_Singleton_service_is_service_defined_get_service(self):
        """
        test register_singleton_service/is_service_defined/get_service
        """

        service_name_one = "service_name_one"
        assert (
            self.strategy_to_test.is_service_defined(service_name_one) is False
        ), "service_name_one initially undefined"
        with pytest.raises(ServiceException):
            self.strategy_to_test.get_service(service_name_one)

        # register a service
        self.strategy_to_test.register_singleton_service(service_name_one, MockService)
        assert (
            self.strategy_to_test.is_service_defined(service_name_one) is True
        ), "service_name_one defined after singleton registration"
        service_one = self.strategy_to_test.get_service(service_name_one)
        assert (
            isinstance(service_one, MockService) is True
        ), "service_one is an instance of MockService"

        # Verify singleton behavior
        service_one_again = self.strategy_to_test.get_service(service_name_one)
        assert service_one is service_one_again, "service_one is a singleton service"

        # register a service with config
        service_name_two = "service_name_two"
        self.strategy_to_test.register_singleton_service(
            service_name_two, MockService, self.__class__.test_service_configuration
        )
        assert (
            self.strategy_to_test.is_service_defined(service_name_one) is True
        ), "service_name_one defined after singleton registration with config"
        service_two = self.strategy_to_test.get_service(service_name_two)
        assert (
            isinstance(service_two, MockService) is True
        ), "service_two is an instance of MockService"

        # Override a service
        self.__class__.mock_service_lifecycle_controller.reset_mock()
        self.strategy_to_test.register_singleton_service(
            service_name_one,
            MockService,
            self.__class__.test_service_configuration,
            True,
        )
        assert (
            self.strategy_to_test.is_service_defined(service_name_one) is True
        ), "service_name_one defined after singleton registration with override"
        service_one = self.strategy_to_test.get_service(service_name_one)
        assert (
            isinstance(service_one, MockService) is True
        ), "service_one is an instance of MockService"

        # test overriding with registering with service with controller only
        self.__class__.mock_service_lifecycle_controller.reset_mock()
        self.strategy_to_test.register_service_by_controller_only(
            service_name_two, self.__class__.mock_service_lifecycle_controller, True
        )

        assert (
            self.strategy_to_test.is_service_defined(service_name_two) is True
        ), "service_name_two defined after registration by class with override by controller only"
        self.strategy_to_test.get_service(service_name_two)
        self.__class__.mock_service_lifecycle_controller.init.assert_not_called()
        self.__class__.mock_service_lifecycle_controller.get_service.assert_called_once()

        # test service error for False override

        with pytest.raises(ServiceException):
            self.strategy_to_test.register_singleton_service(
                service_name_one,
                MockService,
                self.__class__.test_service_configuration,
                False,
            )
        with pytest.raises(ServiceException):
            self.strategy_to_test.register_singleton_service(
                service_name_one, MockService, self.__class__.test_service_configuration
            )
        with pytest.raises(ServiceException):
            self.strategy_to_test.register_singleton_service(
                service_name_two, MockService, self.__class__.test_service_configuration
            )

    def test_shutdown(self):
        """
        test shutdown
        """
        service_name_one = "service_name_one"

        # register a service
        self.strategy_to_test.register_service(
            service_name_one,
            self.__class__.mock_service_instance_provider,
            self.__class__.mock_service_lifecycle_controller,
        )
        assert (
            self.strategy_to_test.is_service_defined(service_name_one) is True
        ), "service_name_one defined after registration"
        service_one = self.strategy_to_test.get_service(service_name_one)

        self.strategy_to_test.shutdown()
        self.__class__.mock_service_lifecycle_controller.shutdown.assert_called_once()

        # Once it's shutdown, expect methods to throw errors
        with pytest.raises(ServiceException):
            self.strategy_to_test.get_service(service_name_one)
