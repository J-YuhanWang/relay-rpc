package dev.yuhanwang.relayrpc.config;

/**
 * Immutable RPC framework configuration: Define the immutable RpcConfig object after instantiation.
 *
 * @param name       framework name
 * @param version    framework version
 * @param serverHost server host
 * @param serverPort server port
 */
public record RpcConfig(String name,
                        String version,
                        String serverHost,
                        int serverPort) {

    /**
     * Create an RPC configuration with default values
     * @return the default RPC configuration
     */
    public static RpcConfig defaults(){
        return new RpcConfig(
                "relay-rpc",
                "1.0",
                "localhost",
                8080
        );
    }
}
