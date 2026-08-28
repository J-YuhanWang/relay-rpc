package dev.yuhanwang.relayrpc;

import dev.yuhanwang.relayrpc.config.RpcConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RpcApplicationTest {

    @Test
    @DisplayName("Should store and return the custom RPC configuration")
    void initialize_withCustomConfig_returnsSameInstance(){
        //arrange
        RpcConfig customConfig = new RpcConfig("custom-rpc","2.0","127.0.0.1",8081);

        //act
        RpcApplication.initialize(customConfig);
        RpcConfig result = RpcApplication.getConfig();

        //assert
        assertSame(customConfig,result);
    }

    @Test
    @DisplayName("Should reject a null custom RPC configuration")
    void initialize_withNullConfig_throwsException() {
        //arrange
        RpcConfig customConfig = null;

        //act
        NullPointerException exception = assertThrows(NullPointerException.class,
                ()->RpcApplication.initialize(customConfig));

        //assert
        assertTrue(exception.getMessage().contains("RPC config must not be null"));
    }

    @Test
    @DisplayName("Should initialize RelayRPC from classpath configuration")
    void initialize_withoutCustomConfig_loadsClasspathConfig() {
        //arrange: configuration is provided by src/test/resources/application.properties.
        //act
        RpcApplication.initialize();
        RpcConfig result = RpcApplication.getConfig();

        //Assert
        assertEquals("relay-rpc-test",result.name());
        assertEquals("2.0",result.version());
        assertEquals("127.0.0.1",result.serverHost());
        assertEquals(9090,result.serverPort());
    }


}
