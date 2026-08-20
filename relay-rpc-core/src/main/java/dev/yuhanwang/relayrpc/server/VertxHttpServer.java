package dev.yuhanwang.relayrpc.server;

import io.vertx.core.Vertx;
import lombok.extern.slf4j.Slf4j;

/**
 * vert.x HttpServer implementation
 */
@Slf4j
public class VertxHttpServer implements HttpServer{
    @Override
    public void doStart(int port) {
        //1. create the core vert.x engine instance
        Vertx vertx = Vertx.vertx();

        //2. create an HTTP server
        io.vertx.core.http.HttpServer server = vertx.createHttpServer();


        //3. bind a request handler
        server.requestHandler(new HttpServerHandler());

        //4. listen on the specific port
        server.listen(port)
                .onSuccess(s->
                        log.info("RelayRPC Server started on port {}", port))
                .onFailure(err->
                        log.error("Failed to start server on port {}", port, err));
    }
}
