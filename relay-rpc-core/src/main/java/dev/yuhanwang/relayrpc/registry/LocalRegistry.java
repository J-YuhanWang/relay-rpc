package dev.yuhanwang.relayrpc.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Local service registry center
 */
public class LocalRegistry {
    private static final Map<String,Class<?>> map= new ConcurrentHashMap<>();

    //LocalRegistry.get(), LocalRegistry.register(),LocalRegistry.remove()

    /**
     * Get service by service name
     * @param serviceName service name
     * @return class of serviceImpl
     */
    public static Class<?> get(String serviceName){
        return map.get(serviceName);
    }

    /**
     * Register the service
     * @param serviceName service name
     * @param implClass class of serviceImpl
     */
    public static void register(String serviceName, Class<?> implClass){
        map.put(serviceName,implClass);
    }

    /**
     * Remove service by service name
     * @param serviceName service name
     */
    public static void remove(String serviceName){
        map.remove(serviceName);
    }
}
