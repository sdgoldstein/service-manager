"""
    Test thje service_configuration module
"""

from service_configuration import DefaultServiceConfigurationImpl


class TestDefaultServiceConfigurationImpl:
    """
    Test DefaultServiceConfigurationImpl
    """

    def test_constructor_and_poperty_map(self):
        """
        test_constructor and poperty_map method
        """

        # try empty configuration
        service_configuration_to_test = DefaultServiceConfigurationImpl({})
        property_map = service_configuration_to_test.poperty_map()
        assert len(property_map) == 0, "Empty configuration has a 0 length property map"

        # try with set properties
        service_configuration_to_test = DefaultServiceConfigurationImpl(
            {"key1": "value1", "key2": "value2"}
        )
        property_map = service_configuration_to_test.poperty_map()

        assert len(property_map) == 2, "Configuration property map has 2 items"
        assert (
            property_map.get("key1") == "value1"
        ), "Configuration property 1 is correct"
        assert (
            property_map.get("key2") == "value2"
        ), "Configuration property 2 is correct"
