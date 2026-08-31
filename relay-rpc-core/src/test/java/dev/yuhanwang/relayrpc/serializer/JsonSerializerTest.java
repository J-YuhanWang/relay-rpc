package dev.yuhanwang.relayrpc.serializer;

import dev.yuhanwang.relayrpc.model.RpcRequest;
import dev.yuhanwang.relayrpc.model.RpcResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class JsonSerializerTest {
    private final Serializer serializer = new JsonSerializer();

    public record TestUser(Long id, String name){}

    @Test
    void deserialize_withMixedRequestArguments_restoresDeclaredTypes() throws IOException {
        // Arrange
        TestUser testUser = new TestUser(1L,"Blair");
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName("TestService")
                .methodName("createUser")
                .parameterTypes(new Class<?>[]{Long.class, TestUser.class})
                .args(new Object[]{100L, testUser})
                .build();

        // Act - serialize request, then deserializer the request
        byte[] bytes = serializer.serialize(rpcRequest);
        RpcRequest result = serializer.deserialize(bytes,RpcRequest.class);

        //assert
        assertInstanceOf(Long.class, result.getArgs()[0]);
        assertInstanceOf(TestUser.class,result.getArgs()[1]);

        assertEquals(100L, result.getArgs()[0]);
        assertEquals(testUser, result.getArgs()[1]);
    }

    //serialize and deserialize response
    @Test
    void deserialize_withCustomResponseData_restoresDeclaredType() throws IOException {
        //arrange
        TestUser user = new TestUser(1L,"Blair");
        RpcResponse rpcResponse = RpcResponse.builder()
                .data(user)
                .dataType(TestUser.class)
                .message("OK")
                .build();

        //Act
        byte[] bytes = serializer.serialize(rpcResponse);
        RpcResponse result = serializer.deserialize(bytes, RpcResponse.class);

        //assert
        assertInstanceOf(TestUser.class,result.getData());

        assertEquals(user,result.getData());
        assertEquals(TestUser.class,result.getDataType());
    }

    @Test
    void deserialize_withNoArguments_doesNotThrow() throws IOException {
        //arrange
        RpcRequest rpcRequest = RpcRequest.builder()
                .args(null)
                .serviceName("TestService")
                .methodName("healthCheck")
                .parameterTypes(new Class<?>[0])
                .build();

        //act
        byte[] bytes = serializer.serialize(rpcRequest);

        //assert
        assertDoesNotThrow(
                ()->serializer.deserialize(bytes, RpcRequest.class)
        );
    }

    @Test
    void deserialize_withNullResponseData_doesNotThrow() throws IOException {
        //arrange
        RpcResponse rpcResponse = RpcResponse.builder()
                .data(null)
                .dataType(null)
                .build();

        //act
        byte[] bytes = serializer.serialize(rpcResponse);

        //assert
        assertDoesNotThrow(
                ()->serializer.deserialize(bytes, RpcResponse.class)
        );
    }

    @Test
    void deserialize_withMismatchedArgsMetadata_throwsIOException() throws IOException {
        //arrange
        RpcRequest rpcRequest = RpcRequest.builder()
                .args(new Object[]{1L,2L})
                .parameterTypes(new Class<?>[]{Long.class})
                .serviceName("TestService")
                .methodName("invalidMethod")
                .build();

        //act
        byte[] bytes = serializer.serialize(rpcRequest);
        //assert
        IOException exception = assertThrows(IOException.class,()->serializer.deserialize(bytes, RpcRequest.class));

        assertTrue(exception.getMessage().contains("argument count"));
    }

}
