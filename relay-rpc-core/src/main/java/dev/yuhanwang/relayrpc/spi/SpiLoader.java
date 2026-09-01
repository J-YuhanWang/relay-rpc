package dev.yuhanwang.relayrpc.spi;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public final class SpiLoader {
    //load the serializer from classpath
    private static final String SYSTEM_SPI_DIR = "META-INF/relayrpc/system/";
    private static final String CUSTOM_SPI_DIR = "META-INF/relayrpc/custom/";

    /**
     * System resources are loaded first so that custom resources can override the same key.
     */
    private static final List<String> SCAN_DIRS = List.of(SYSTEM_SPI_DIR, CUSTOM_SPI_DIR);

    /**
     * Service interface -> key -> implementation class
     * <p>
     * Serializer.class
     * ↓
     * {
     * "jdk"  → JdkSerializer.class,
     * "json" → JsonSerializer.class
     * }
     */
    private static final Map<Class<?>, Map<String, Class<?>>> IMPLEMENTATION_CACHE = new ConcurrentHashMap<>();

    /**
     * Implementation class -> reusable instance.
     * (JdkSerializer.class -> JdkSerializer instance)
     */
    private static final Map<Class<?>, Object> INSTANCE_CACHE = new ConcurrentHashMap<>();

    private SpiLoader() {
    }

    /**
     * Returns a cached SPI implementation for the provided key.
     *
     * @param serviceType SPI service interface
     * @param key         registered implementation key
     * @param <T>         SPI service type
     * @return the cached implementation instance
     */
    public static <T> T getInstance(Class<T> serviceType, String key) {
        //Serializer serializer = SpiLoader.getInstance(Serializer.class, "json");
        // 1. Check parameters
        if (serviceType == null) {
            throw new IllegalArgumentException("SPI service type must not be null");
        }
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("SPI implementation key must not be blank");
        }
        String normalizedKey = key.trim();

        // 2. Locate the implementation class registry for this interface
        Map<String, Class<?>> keyClassMap = IMPLEMENTATION_CACHE.get(serviceType);
        if (keyClassMap == null) {
            throw new IllegalStateException("SPI service type has not been loaded: " + serviceType.getName());
        }

        // 3. Find the corresponding implementation class based on the key
        Class<?> implementationClass = keyClassMap.get(normalizedKey);
        if (implementationClass == null) {
            throw new IllegalArgumentException("No SPI implementation registered for key '"
                    + normalizedKey
                    + "' and service "
                    + serviceType.getName()
                    + ". Available keys: "
                    + keyClassMap.keySet());
        }

        // 4. Retrieve or create an object from the instance cache
        Object instance = INSTANCE_CACHE.computeIfAbsent(
                implementationClass,
                clazz -> {
                    try{
                        return clazz.getDeclaredConstructor().newInstance();
                    }catch(ReflectiveOperationException e){
                        throw new IllegalStateException("Failed to create SPI implementation: "+clazz.getName(),e);
                    }
                }
        );

        // 5. Convert to interface type and return
        return serviceType.cast(instance);
    }

    /**
     * Loads and registers all implementations of the given SPI service.
     * -> load(Serializer.class)
     *
     * @param serviceType SPI service interface
     * @return registered implementation keys and classes
     */
    public static Map<String, Class<?>> load(Class<?> serviceType) {
        //Null check
        if (serviceType == null) {
            throw new IllegalArgumentException("SPI service type must not be null");
        }
        //1.Get the fully qualified name of the interface
        String serviceName = serviceType.getName();

        //2.Create a Map to store the key and implementation class
        Map<String, Class<?>> keyClassMap = new HashMap<>();

        //3.Access SPI resources and implementation classes on the runtime classpath
        ClassLoader classLoader = SpiLoader.class.getClassLoader();

        //4.Scan the system and custom directories
        for (String scanDir : SCAN_DIRS) {
            //5.Locate the SPI configuration file corresponding to the current interface
            String resourceName = scanDir + serviceName;
            try {
                Enumeration<URL> resources = classLoader.getResources(resourceName);
                //6.Read the configuration file
                while (resources.hasMoreElements()) {
                    URL resource = resources.nextElement();
                    Properties properties = new Properties();
                    //Convert the class name to a Class object
                    try (
                            InputStream inputStream = resource.openStream();
                            InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                    ) {
                        properties.load(reader);//example:properties.getProperty("jdk")
                        // dev.yuhanwang.relayrpc.serializer.JsonSerializer
                    }
                    for (String key : properties.stringPropertyNames()) {
                        //json → JsonSerializer.class
                        String className = properties.getProperty(key).trim();

                        Class<?> implementationClass = classLoader.loadClass(className);

                        // Ensure that the registered class implements the requested SPI interface
                        if (!serviceType.isAssignableFrom(implementationClass)) {
                            throw new IllegalStateException(implementationClass.getName()
                                    + " does not implement "
                                    + serviceType.getName());
                        }
                        keyClassMap.put(key.trim(), implementationClass);
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                throw new IllegalStateException("Failed to load SPI implementations for " + serviceName, e);
            }

        }
        //Save to the global cache
        Map<String, Class<?>> loadedImplementations = Map.copyOf(keyClassMap);
        IMPLEMENTATION_CACHE.put(serviceType, loadedImplementations);

        log.debug("Loaded {} SPI implementations for {}: {}",
                keyClassMap.size(), serviceName, keyClassMap.keySet());

        //Return the loading result
        return loadedImplementations;

    }


}
