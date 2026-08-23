package dev.yuhanwang.relayrpc.serializer;



import dev.yuhanwang.relayrpc.model.RpcRequest;
import dev.yuhanwang.relayrpc.model.RpcResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Serializable;

import static org.junit.jupiter.api.Assertions.*;

public class JdkSerializerTest {
    private final Serializer serializer = new JdkSerializer();

    @Test
    @DisplayName("Should serialize and deserialize a standard RpcRequest correctly")
    void testRpcRequestSerialization() throws IOException {
        // Arrange
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName("TestService")
                .methodName("getUserById")
                .parameterTypes(new Class<?>[]{Long.class})
                .args(new Object[]{1L})
                .build();

        // Act
        byte[] bodyBytes = serializer.serialize(rpcRequest);
        RpcRequest result = serializer.deserialize(bodyBytes,RpcRequest.class);

        // Assert
        assertNotNull(result);
        assertEquals("TestService",result.getServiceName());
        assertEquals("getUserById",result.getMethodName());
        assertEquals(Long.class,result.getParameterTypes()[0]);
        assertEquals(1L,result.getArgs()[0]);
    }

    @Test
    @DisplayName("Should serialize and deserialize a standard RpcResponse with return data")
    void testRpcResponseSerialization() throws IOException {
        //Arrange
        RpcResponse rpcResponse = RpcResponse.builder()
                .data("success_data")
                .dataType(String.class)
                .message("OK")
                .build();
        //act
        byte[] bodyBytes = serializer.serialize(rpcResponse);
        RpcResponse result = serializer.deserialize(bodyBytes, RpcResponse.class);
        //assert
        assertNull(result.getException());
        assertEquals("success_data",result.getData());
        assertEquals("OK",result.getMessage());
        assertEquals(String.class,result.getDataType());
    }

    @Test
    @DisplayName("Should handle RpcRequest with empty args for zero-argument methods")
    void testRpcRequestWithEmptyArgs() throws Exception {
        // Arrange
        RpcRequest request = RpcRequest.builder()
                .serviceName("OrderService")
                .methodName("getAllOrders")
                .parameterTypes(new Class<?>[]{})
                .args(new Object[]{})
                .build();

        // Act
        byte[] bytes = serializer.serialize(request);
        RpcRequest result = serializer.deserialize(bytes, RpcRequest.class);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getParameterTypes().length);
        assertEquals(0, result.getArgs().length);
    }

    @Test
    @DisplayName("Should handle RpcResponse with null data when method returns void")
    void testRpcResponseWithNullData() throws Exception {
        // Arrange
        RpcResponse response = RpcResponse.builder()
                .data(null)
                .message("OK")
                .build();

        // Act
        byte[] bytes = serializer.serialize(response);
        RpcResponse result = serializer.deserialize(bytes, RpcResponse.class);

        // Assert
        assertNotNull(result);
        assertNull(result.getData());
        assertEquals("OK", result.getMessage());
    }

    @Test
    @DisplayName("Should correctly serialize nested custom POJO objects inside args")
    void testRpcRequestWithNestedSerializableObject() throws Exception {
        // Arrange
        MockUser user = new MockUser("Blair");
        RpcRequest request = RpcRequest.builder()
                .serviceName("UserService")
                .methodName("getUser")
                .args(new Object[]{user})
                .build();

        // Act
        byte[] bytes = serializer.serialize(request);
        RpcRequest result = serializer.deserialize(bytes, RpcRequest.class);

        // Assert
        assertNotNull(result);
        MockUser restoredUser = (MockUser) result.getArgs()[0];
        assertEquals("Blair", restoredUser.name());
    }

    @Test
    @DisplayName("Should correctly serialize and restore Exception objects for error handling")
    void testRpcResponseWithException() throws Exception {
        // Arrange
        RuntimeException testException = new RuntimeException("Target server internal error");
        RpcResponse response = RpcResponse.builder()
                .exception(testException)
                .message("ERROR")
                .build();

        // Act
        byte[] bytes = serializer.serialize(response);
        RpcResponse result = serializer.deserialize(bytes, RpcResponse.class);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getException());
        assertEquals("Target server internal error", result.getException().getMessage());
        assertEquals("ERROR", result.getMessage());
    }

    private record MockUser(String name) implements Serializable {}
}


