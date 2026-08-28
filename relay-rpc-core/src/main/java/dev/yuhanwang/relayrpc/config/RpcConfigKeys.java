package dev.yuhanwang.relayrpc.config;

/**
 * Define property keys for RPC configuration
 */
public final class RpcConfigKeys {
    private static final String PREFIX = "relay.rpc";

    public static final String NAME= PREFIX + ".name";

    public static final String VERSION = PREFIX + ".version";

    public static final String SERVER_HOST = PREFIX + ".server.host";

    public static final String SERVER_PORT = PREFIX + ".server.port";

    /**
     * Prevents instantiation.
     */
    private RpcConfigKeys(){

    }
}
