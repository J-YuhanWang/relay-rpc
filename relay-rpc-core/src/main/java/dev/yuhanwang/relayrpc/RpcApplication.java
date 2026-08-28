package dev.yuhanwang.relayrpc;

import dev.yuhanwang.relayrpc.config.RpcConfig;
import dev.yuhanwang.relayrpc.config.RpcConfigLoader;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * Provides access to the global RelayRPC configuration.
 */
@Slf4j
public final class RpcApplication {

    private static RpcConfig config;

    private RpcApplication(){
    }

    /**
     * Initializes RelayRPC from the classpath configuration.
     * write the config
     */
    public static void initialize(){
        initialize(RpcConfigLoader.load());
    }

    /**
     * Initializes RelayRPC with a custom configuration.
     *
     * @param customConfig custom RPC configuration
     */
    public static void initialize(RpcConfig customConfig){
        config = Objects.requireNonNull(customConfig, "RPC config must not be null");
        log.info("RelayRPC initialized: name={}, version={}, server={}:{}",
                config.name(),
                config.version(),
                config.serverHost(),
                config.serverPort()
        );
    }

    /**
     * Returns the global RPC configuration,
     * loading it lazily if necessary.
     *
     * @return the global RPC configuration
     */
    public static RpcConfig getConfig(){
        if(config==null){
            initialize();
        }

        return config;
    }


}
