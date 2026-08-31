package dev.yuhanwang.relayrpc.serializer;


import dev.yuhanwang.relayrpc.model.RpcRequest;
import dev.yuhanwang.relayrpc.model.RpcResponse;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

public final class JsonSerializer implements Serializer{

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public <T> byte[] serialize(T object) throws IOException {

        return OBJECT_MAPPER.writeValueAsBytes(object);
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> type) throws IOException {
        T object = OBJECT_MAPPER.readValue(bytes,type);
        
        if(object instanceof RpcRequest rpcRequest){
            restoreRequestArgumentTypes(rpcRequest);
        }
        
        if(object instanceof RpcResponse rpcResponse){
            restoreResponseDataType(rpcResponse);
        }


        return object;
    }

    private static void restoreResponseDataType(RpcResponse rpcResponse) {
        Object data=rpcResponse.getData();
        Class<?> dataType = rpcResponse.getDataType();
        if(data==null || dataType==null){
            return;
        }
        rpcResponse.setData(OBJECT_MAPPER.convertValue(data,dataType));
    }

    private static void restoreRequestArgumentTypes(RpcRequest rpcRequest) throws IOException {
        Class<?>[] parameterTypes = rpcRequest.getParameterTypes();
        Object[] args = rpcRequest.getArgs();

        //null protection
        if(args==null || parameterTypes==null){
            return;
        }
        //check array lengths
        if(parameterTypes.length != args.length){
            throw new IOException("RPC argument count does not match parameter type count");
        }
        //convert every non-null argument and assign the converted value to args[i]
        for(int i=0;i<args.length;i++){
            if(args[i]==null){
                continue;
            }
            args[i] = OBJECT_MAPPER.convertValue(args[i],parameterTypes[i]);
        }
    }
}
