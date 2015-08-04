package com.jinloes;

import io.vertx.core.*;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

/**
 * Created by jinloes on 7/31/15.
 */
public class Server extends AbstractVerticle {
    public static void main(String[] args) {
        Vertx.clusteredVertx(new VertxOptions(), event -> {
            Vertx vertx1 = event.result();
            vertx1.deployVerticle(new Server());
            vertx1.deployVerticle(new ToDoService());
        });

    }

    @Override
    public void start() throws Exception {
        Router router = Router.router(vertx);

        router.route().handler(BodyHandler.create());
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
