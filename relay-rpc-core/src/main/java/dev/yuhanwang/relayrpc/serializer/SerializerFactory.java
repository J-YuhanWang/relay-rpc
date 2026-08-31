package dev.yuhanwang.relayrpc.serializer;

import java.util.Map;

public final class SerializerFactory {
    private SerializerFactory(){}

    //"jdk" - JdkSerializer, "json" - JsonSerializer
    private static final Map<String,Serializer> SERIALIZERS = Map.of(
            SerializerKeys.JDK, new JdkSerializer(),
            SerializerKeys.JSON, new JsonSerializer()
    );

    /**
     * Returns the serializer registered for the given key.
     *
     * @param key serializer key
     * @return registered serializer instance
     */
    public static Serializer getInstance(String key){
        // Validate key
        if(key==null || key.isBlank()){
            throw new IllegalArgumentException("Serializer key must not be blank");
        }

        // Find serializer
        Serializer serializer = SERIALIZERS.get(key);
        // Throw if unsupported
        if(serializer==null){
            throw new IllegalArgumentException("Unsupported serializer: " + key);
        }
        // Return serializer
        return serializer;
    }
}
