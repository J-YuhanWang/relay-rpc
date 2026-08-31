package dev.yuhanwang.relayrpc.proxy;

import dev.yuhanwang.relayrpc.RpcApplication;
import dev.yuhanwang.relayrpc.config.RpcConfig;
import dev.yuhanwang.relayrpc.model.RpcRequest;
import dev.yuhanwang.relayrpc.model.RpcResponse;
import dev.yuhanwang.relayrpc.serializer.Serializer;
import dev.yuhanwang.relayrpc.serializer.SerializerFactory;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Universal dynamic proxy invocation handler using JDK reflection
 */
@Slf4j
public class ServiceProxy implements InvocationHandler {

//    private static final String SERVER_URL = "http://localhost:8080";
    //1.specify serializer
    private final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getConfig().serializer());

    //Singleton reuse connection pool
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    private URI getServerUri(){
        RpcConfig config = RpcApplication.getConfig();
        String serverUrl = "http://"+config.serverHost()+":"+config.serverPort();
        return URI.create(serverUrl);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args){

        //2. automatically assemble RpcRequest using the method and args passed in by the JVM
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();
        URI serverUri = getServerUri();

        log.info("Sending RPC request to {}",serverUri);

        try {
            // serialize the rpcRequest to byte[]
            byte[] bodyBytes = serializer.serialize(rpcRequest);

            //send http post request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(serverUri)
                    .header("Content-Type", "application/octet-stream")
                    .POST(HttpRequest.BodyPublishers.ofByteArray(bodyBytes))
                    .build();

            //receive the http response: 1)convert the binary data streams to byte[] through BodyHandlers.ofByteArray
            HttpResponse<byte[]> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofByteArray());

            // 2) deserialize the httpResponse to rpcResponse
            RpcResponse rpcResponse = serializer.deserialize(response.body(), RpcResponse.class);

            return rpcResponse != null ? rpcResponse.getData() : null;
        }catch(Exception e){
            log.error("Dynamic proxy failed to execute RPC call for method: {}", method.getName(), e);
        }
        return null;
    }
}
