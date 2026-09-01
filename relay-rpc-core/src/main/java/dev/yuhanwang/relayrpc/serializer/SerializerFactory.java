package dev.yuhanwang.relayrpc.serializer;

import dev.yuhanwang.relayrpc.spi.SpiLoader;

import java.util.Map;

public final class SerializerFactory {
    private SerializerFactory(){}

    //"jdk" - JdkSerializer, "json" - JsonSerializer
    static{
        SpiLoader.load(Serializer.class);
    }

    /**
     * Returns the serializer registered for the given key.
     *
     * @param key serializer key
     * @return registered serializer instance
     */
    public static Serializer getInstance(String key){

        return SpiLoader.getInstance(Serializer.class,key);
    }
}
