package com.jinloes;

import io.vertx.core.*;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

import java.lang.management.ManagementFactory;

/**
 * Created by rr2re on 9/3/2015.
 */
public class Server2 extends AbstractVerticle {
    public static void main(String[] args) {
        Vertx.clusteredVertx(new VertxOptions().setClustered(true).setHAEnabled(true), event -> {
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

        vertx.createHttpServer().requestHandler(router::accept).listen(8181);
    }
}
