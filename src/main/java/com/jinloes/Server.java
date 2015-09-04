package com.jinloes;

import com.hazelcast.config.Config;
import io.vertx.core.*;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

import java.lang.management.ManagementFactory;

/**
 * Created by jinloes on 7/31/15.
 */
public class Server extends AbstractVerticle {
    public static void main(String[] args) {
        Config config = new Config();
        config.getNetworkConfig().getJoin().getTcpIpConfig().setEnabled(true).addMember("10.0.0.3:5701");
        config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
        Vertx.clusteredVertx(new VertxOptions().setClusterManager(new HazelcastClusterManager(config))
                        .setClustered(true).setHAEnabled(true),
                event -> {
                    Vertx vertx1 = event.result();
                    vertx1.deployVerticle(new Server());
                    vertx1.deployVerticle(new ToDoService());
                    vertx1.deployVerticle(PrintingService.class.getCanonicalName());
                });

    }

    @Override
    public void start() throws Exception {
        Router router = Router.router(vertx);

        router.route().handler(BodyHandler.create());
        router.post("/deployprinter").handler(routingContext -> {
            vertx.deployVerticle(new PrintingService());
            routingContext.response().end();
        });
        router.get("/getconsumer").handler(routingContext -> {
            HttpServerResponse response =  routingContext.response();
            final String name = ManagementFactory.getRuntimeMXBean().getName();
            JsonObject object = new JsonObject()
                    .put("running on", "Running on " + name);
            response.end(object.toString());
        });
        router.get("/todos/:toDoId").handler(routingContext ->
                vertx.eventBus().send("getTodo", routingContext.request().getParam("todoId"),
                        new Handler<AsyncResult<Message<JsonObject>>>() {
                            @Override
                            public void handle(AsyncResult<Message<JsonObject>> result) {
                                HttpServerResponse response = routingContext.response();
                                response.putHeader("content-type", "application/json")
                                        .end(result.result().body().encodePrettily());
                            }
                        }));
        router.post("/todos").handler(routingContext ->
                vertx.eventBus().send("createTodo", "", result -> {
                    HttpServerResponse httpServerResponse = routingContext.response();
                    httpServerResponse.putHeader("content-type", "application/json")
                            .end(new JsonObject().put("message", result.result().body())
                                    .encodePrettily());
                }));

        vertx.createHttpServer().requestHandler(router::accept).listen(8080);
    }
}
