package dev.yuhanwang.relayrpc.serializer;


import java.io.IOException;

/**
 * Serializer interface for converting object to/from byte arrays (http byte sequence)
 */
public interface Serializer {

    /**
     * Serialize an object to byte array
     * @param object the object to serialize (rpcResponse/rpcRequest)
     * @return byte array: representation of the object
     * @param <T> the generic method declaration
     * @throws IOException if serialization fails
     */
    <T> byte[] serialize(T object) throws IOException;

    /**
     * Deserialize the byte array into an object of a specific type
     * @param bytes: the byte array to deserialize
     * @param type: the target class type
     * @return the deserialized object
     * @param <T> the generic type
     * @throws IOException IOException if deserialization fails
     */
    <T> T deserialize(byte[] bytes,Class<T> type) throws IOException;
}
