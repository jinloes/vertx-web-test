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
public class Server2 {
    public static void main(String[] args) {
        Vertx.clusteredVertx(new VertxOptions().setHAEnabled(true), event -> {
        });
    }
}
