package dev.yuhanwang.relayrpc.proxy;

import java.lang.reflect.Proxy;

/**
 * Factory class to create dynamic proxy for service interfaces
 */

public class ServiceProxyFactory {

    /**
     * Obtain the dynamic proxy object based on the service interface class
     * @param serviceClass The Class object of the interface
     * @return the service instance after proxying
     * @param <T> the generic type of the interface
     */
    @SuppressWarnings("unchecked")
    public static <T> T getProxy(Class<T> serviceClass){
        // factory pattern: decoupled the process of instance creation, only exposed the interface
        return (T) Proxy.newProxyInstance(
                serviceClass.getClassLoader(),
                new Class<?>[]{serviceClass},
                new ServiceProxy()
        );
    }
}
