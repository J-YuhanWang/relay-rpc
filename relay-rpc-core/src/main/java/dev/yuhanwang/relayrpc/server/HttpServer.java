package dev.yuhanwang.relayrpc.server;

/**
 * HttpServer interface defining the server startup contract
 */
public interface HttpServer {

    /**
     * Start the server listening on the specific port
     * @param port: listening port
     */
    void doStart(int port);

}
