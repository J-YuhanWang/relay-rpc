package dev.yuhanwang.relayrpc.example.consumer;


import dev.yuhanwang.relayrpc.example.common.model.User;
import dev.yuhanwang.relayrpc.example.common.service.UserService;
import dev.yuhanwang.relayrpc.model.RpcRequest;
import dev.yuhanwang.relayrpc.model.RpcResponse;
import dev.yuhanwang.relayrpc.serializer.JdkSerializer;
import dev.yuhanwang.relayrpc.serializer.Serializer;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Static proxy
 */
@Slf4j
public class UserServiceProxy implements UserService {
    // specify the serializer
    Serializer serializer = new JdkSerializer();

    @Override
    public User getUserById(Long id) {
        // assemble RpcRequest instance
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(UserService.class.getName())
                .methodName("getUserById")
                .parameterTypes(new Class<?>[]{Long.class})
                .args(new Object[]{id})
                .build();

        RpcResponse rpcResponse = sendRpcRequest(rpcRequest);
        return rpcResponse != null ? (User) rpcResponse.getData() : null;

    }

    @Override
    public Long createUser(User user) {
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(UserService.class.getName())
                .methodName("createUser")
                .parameterTypes(new Class<?>[]{User.class})
                .args(new Object[]{user})
                .build();

        RpcResponse rpcResponse = sendRpcRequest(rpcRequest);
        return rpcResponse != null ? (Long) rpcResponse.getData() : null;
    }

    private RpcResponse sendRpcRequest(RpcRequest rpcRequest) {
        // serialize rpcRequest object to byte sequence
        try {
            byte[] bodyBytes = serializer.serialize(rpcRequest);

            // send rpc request by http client
            // 1. create JDK native httpclient
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(5))
                    .build();
            // 2. construct standard http post request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080"))
                    .header("Content-Type", "application/octet-stream")
                    .POST(HttpRequest.BodyPublishers.ofByteArray(bodyBytes))
                    .build();

            // 3.send and receive the byte sequence response(byte[])
            HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

            // 4.deserialize
            return serializer.deserialize(response.body(), RpcResponse.class);

        } catch (Exception e) {
            log.error("RPC request failed across network", e);
        }
        return null;
    }
}
