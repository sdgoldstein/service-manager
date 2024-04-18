"""
    Test the service_manager module
"""

from unittest.mock import Mock

import pytest

from sphyrna.service_manager.service_configuration import (
    EMPTY_SERVICE_CONFIGURATION,
    DefaultServiceConfigurationImpl,
    ServiceConfiguration,
)
from sphyrna.service_manager.service_manager import ServiceManager
from sphyrna.service_manager.service_manager_strategy import ServiceManagerStrategy


class TestServiceManager:
    """
    Test ServiceManager
    """

    @classmethod
    def setup_class(cls):
        """
        Set code run once before any test methods in the class
        """
        cls.mock_service_manager_strategy = Mock(ServiceManagerStrategy)

    def test_set_default_strategy_get_service(self):
        """
        test setting the default strategy
        """

        ServiceManager.set_default_strategy(
            self.__class__.mock_service_manager_strategy
        )
        ServiceManager.get_service("someservice")
        self.__class__.mock_service_manager_strategy.get_service.assert_called_once_with(
            "someservice", EMPTY_SERVICE_CONFIGURATION
        )

        # Test with a config
        service_config: ServiceConfiguration = DefaultServiceConfigurationImpl(
            {"key1": "value1", "key2": "value2"}
        )
        ServiceManager.set_default_strategy(
            self.__class__.mock_service_manager_strategy
        )

        self.__class__.mock_service_manager_strategy.reset_mock()
        ServiceManager.get_service("someOtherService", service_config)
        self.__class__.mock_service_manager_strategy.get_service.assert_called_once_with(
            "someOtherService", service_config
        )

    def test_set_default_strategy_is_service_sefined(self):
        """
        test set default strategy with is service defined
        """
        ServiceManager.set_default_strategy(
            self.__class__.mock_service_manager_strategy
        )
        ServiceManager.is_service_defined("someservice")
        self.__class__.mock_service_manager_strategy.is_service_defined.assert_called_once_with(
            "someservice"
        )

    def test_shutdown(self):
        """
        test shutdown
        """
        ServiceManager.set_default_strategy(
            self.__class__.mock_service_manager_strategy
        )
        ServiceManager.shutdown()
        self.__class__.mock_service_manager_strategy.shutdown.assert_called_once()
