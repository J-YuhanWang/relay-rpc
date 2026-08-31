package dev.yuhanwang.relayrpc.server;

import dev.yuhanwang.relayrpc.RpcApplication;
import dev.yuhanwang.relayrpc.model.RpcRequest;
import dev.yuhanwang.relayrpc.model.RpcResponse;
import dev.yuhanwang.relayrpc.registry.LocalRegistry;
import dev.yuhanwang.relayrpc.serializer.Serializer;
import dev.yuhanwang.relayrpc.serializer.SerializerFactory;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * Http Request Handler for processing RPC calls via reflection
 */
@Slf4j
public class HttpServerHandler implements Handler<HttpServerRequest> {
    @Override
    public void handle(HttpServerRequest request) {
        //1.specify the serializer
        Serializer serializer = SerializerFactory.getInstance(RpcApplication.getConfig().serializer());

        //2.asychronously listen for the complete response body
        request.bodyHandler(body->{
            //2.1 read bytes and deserialize
            byte[] bytes = body.getBytes();
            RpcRequest rpcRequest = null;
            try {
                rpcRequest = serializer.deserialize(bytes, RpcRequest.class);
            } catch (Exception e) {
                log.error("Failed to deserialize RpcRequest from client, uri: {}", request.uri(), e);
            }
            //2.2 initialize the response object and null protection
            RpcResponse rpcResponse = new RpcResponse();
            if(rpcRequest==null){
                rpcResponse.setMessage("RpcResponse is null");
                doResponse(request,rpcResponse,serializer);
                return;
            }
            log.info("Received RPC request: {}#{}", rpcRequest.getServiceName(), rpcRequest.getMethodName());

            //2.3 Utilize reflection to find the implementation class in the local registry based on the service name
            try{
                //1. find class from LocalRegistry
                Class<?> implClass = LocalRegistry.get(rpcRequest.getServiceName());
                if(implClass==null){
                    throw new ClassNotFoundException("Service not found in LocalRegistry: " + rpcRequest.getServiceName());
                }
                //2. find method
                Method method = implClass.getMethod(rpcRequest.getMethodName(),rpcRequest.getParameterTypes());
                //3. invoke the method : method.invoke(targetObject, args)
                Object result = method.invoke(implClass.getDeclaredConstructor().newInstance(),rpcRequest.getArgs());

                //4. wrap the response
                rpcResponse.setData(result);
                rpcResponse.setDataType(method.getReturnType());
                rpcResponse.setMessage("ok");
                log.info("Successfully invoked {}#{}", rpcRequest.getServiceName(), rpcRequest.getMethodName());

            } catch (Exception e) {
                log.error("Failed to invoke service method: {} on service: {}", rpcRequest.getMethodName(), rpcRequest.getServiceName(), e);
                rpcResponse.setMessage(e.getMessage());
                rpcResponse.setException(e);
            }
            doResponse(request,rpcResponse,serializer);
        });
    }

    /**
     * Helper funtion for sending http response
     */
    private void doResponse(HttpServerRequest request, RpcResponse rpcResponse, Serializer serializer){
        HttpServerResponse httpServerResponse =request.response().putHeader("content-type","application/octet-stream");
        try{
            byte[] serialized = serializer.serialize(rpcResponse);
            httpServerResponse.end(Buffer.buffer(serialized));
        } catch (IOException e) {
            log.error("Failed to serialize RpcResponse",e);
            httpServerResponse.end(Buffer.buffer());
        }
    }


}
