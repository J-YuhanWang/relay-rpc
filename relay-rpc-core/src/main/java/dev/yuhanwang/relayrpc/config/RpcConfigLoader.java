package dev.yuhanwang.relayrpc.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public final class RpcConfigLoader {
    private static final String CONFIG_FILE = "application.properties";

    //cannot be instantiation through new method
    private RpcConfigLoader(){}

    /**
     * Loads the RPC configuration.
     *
     * @return the resolved RPC configuration
     */
    public static RpcConfig load() {
        // 1. read properties file
        Properties properties = loadProperties();
        return resolve(properties);
    }

    static RpcConfig resolve(Properties properties) {
        // 2. get default configurations
        RpcConfig defaults = RpcConfig.defaults();

        // 3. Resolve user-input values with default fallbacks
        String name = properties.getProperty(RpcConfigKeys.NAME, defaults.name());
        String version = properties.getProperty(RpcConfigKeys.VERSION, defaults.version());
        String serverHost = properties.getProperty(RpcConfigKeys.SERVER_HOST, defaults.serverHost());
        int serverPort = readInteger(properties,RpcConfigKeys.SERVER_PORT, defaults.serverPort());
        String serializer = properties.getProperty(RpcConfigKeys.SERIALIZER,defaults.serializer());

        validatePort(serverPort);

        // 4. create and return RpcConfig
        return new RpcConfig(name,version,serverHost,serverPort,serializer);
    }

    /**
     * Load properties from the configuration file on the classpath(runtime)
     * returns an empty properties if the file does not exist
     */
    private static Properties loadProperties(){
        //read application.properties
        Properties properties = new Properties(); // an empty Properties object
        ClassLoader classLoader = RpcConfigLoader.class.getClassLoader();

        // Locate the configuration file on the runtime classpath.
        try(InputStream inputStream = classLoader.getResourceAsStream(CONFIG_FILE)){
            if(inputStream==null){
                // Use an empty object so the loader can fall back to defaults.
                return properties;
            }

            //Decode the byte stream as UTF-8 characters
            try(InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)){
                properties.load(reader);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load RPC configuration from "+CONFIG_FILE,e);
        }

        // Return the parsed properties.
        return properties;
    }

    /**
     * Reads an integer configuration property.
     *
     * @param properties   configuration properties
     * @param key          property key
     * @param defaultValue value used when the property is missing
     * @return the configured integer or the default value
     */
    private static int readInteger(Properties properties, String key, int defaultValue){
        //1.read the raw string value based on the key
        String value = properties.getProperty(key);

        //2.if value is null/blank, return default value
        if(value == null || value.isBlank()){
            return defaultValue;
        }

        //3.parse the value as an integer
        try{
            return Integer.parseInt(value.trim());
        }catch (NumberFormatException e){
        //throw a meaningful exception if parsing fails
            throw new IllegalArgumentException(
                    "Configuration property '"+ key + "' must be an integer, but was: " + value, e
            );
        }
    }

    /**
     * Validates the server port range
     */
    private static void validatePort(int port){
        if(port<1 || port >65_535){
            throw new IllegalArgumentException(
                    "Server port must be between 1 and 65535, but was: " + port
            );
        }
    }
}
