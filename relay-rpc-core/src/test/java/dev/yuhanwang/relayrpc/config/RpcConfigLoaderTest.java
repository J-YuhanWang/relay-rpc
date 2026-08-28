package dev.yuhanwang.relayrpc.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class RpcConfigLoaderTest {

    @Test
    @DisplayName("Should resolve all user-provided configuration values")
    void resolve_withValidProperties_returnsConfiguredValues(){
        //arrange
        Properties properties = new Properties();
        properties.setProperty(RpcConfigKeys.NAME,"custom-rpc");
        properties.setProperty(RpcConfigKeys.VERSION,"2.0");
        properties.setProperty(RpcConfigKeys.SERVER_HOST,"127.0.0.1");
        properties.setProperty(RpcConfigKeys.SERVER_PORT,"9090");

        //ACT
        RpcConfig result = RpcConfigLoader.resolve(properties);

        //assert
        assertEquals("custom-rpc",result.name());
        assertEquals("2.0",result.version());
        assertEquals("127.0.0.1",result.serverHost());
        assertEquals(9090,result.serverPort());
    }

    @Test
    @DisplayName("Should use default values when properties are missing")
    void resolve_withEmptyProperties_returnsDefaults(){
        //arrange
        Properties properties = new Properties();
        RpcConfig defaults = RpcConfig.defaults();
        //act
        RpcConfig result = RpcConfigLoader.resolve(properties);
        //assert
        assertEquals(defaults,result);
    }

    @Test
    @DisplayName("Should resolve the user-provided values while others use default values")
    void resolve_withPartialProperties_overridesOnlyProvidedValues(){
        //arrange
        Properties properties = new Properties();
        properties.setProperty(RpcConfigKeys.SERVER_PORT,"9090");
        RpcConfig defaults = RpcConfig.defaults();
        //act
        RpcConfig result = RpcConfigLoader.resolve(properties);
        //assert
        assertEquals(defaults.name(),result.name());
        assertEquals(defaults.version(),result.version());
        assertEquals(defaults.serverHost(),result.serverHost());
        assertEquals(9090,result.serverPort());
    }

    @Test
    @DisplayName("Should reject a non-integer server port")
    void resolve_withNonIntegerPort_throwsException() {
        // Arrange
        Properties properties = new Properties();
        properties.setProperty(
                RpcConfigKeys.SERVER_PORT,
                "invalid"
        );

        // Act
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> RpcConfigLoader.resolve(properties)
                );

        // Assert
        assertTrue(exception.getMessage().contains("invalid"));
        assertTrue(exception.getMessage().contains(RpcConfigKeys.SERVER_PORT));
        assertInstanceOf(NumberFormatException.class,exception.getCause());
    }


    @Test
    @DisplayName("Should reject a server port outside the valid range")
    void resolve_withOutOfRangePort_throwsException() {
        // Arrange
        Properties properties = new Properties();
        properties.setProperty(RpcConfigKeys.SERVER_PORT,"70000");

        // Act
        IllegalArgumentException exception =
                assertThrowsExactly(
                        IllegalArgumentException.class,
                        () -> RpcConfigLoader.resolve(properties)
                );

        assertTrue(exception.getMessage().contains("70000"));
    }

    @Test
    @DisplayName("Should load RPC configuration from the classpath")
    void load_withClasspathProperties_returnsConfiguredValues(){
        // Arrange: configuration is provided by src/test/resources/application.properties.
        // Act
        RpcConfig result = RpcConfigLoader.load();
        //Assert
        assertEquals("relay-rpc-test",result.name());
        assertEquals("2.0",result.version());
        assertEquals("127.0.0.1",result.serverHost());
        assertEquals(9090,result.serverPort());
    }
}
