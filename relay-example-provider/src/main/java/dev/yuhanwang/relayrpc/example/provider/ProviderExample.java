package dev.yuhanwang.relayrpc.example.provider;

import dev.yuhanwang.relayrpc.RpcApplication;
import dev.yuhanwang.relayrpc.config.RpcConfig;
import dev.yuhanwang.relayrpc.example.common.service.UserService;
import dev.yuhanwang.relayrpc.registry.LocalRegistry;
import dev.yuhanwang.relayrpc.server.HttpServer;
import dev.yuhanwang.relayrpc.server.VertxHttpServer;

/**
 * Service provider example
 */
public class ProviderExample {
    public static void main(String[] args) {
        RpcApplication.initialize();

        //service registry
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);

        //start the web server
        HttpServer httpServer = new VertxHttpServer();

        RpcConfig config = RpcApplication.getConfig();

        httpServer.doStart(config.serverPort());
    }
}
