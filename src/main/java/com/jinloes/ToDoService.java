package com.jinloes;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jinloes on 7/31/15.
 */
public class ToDoService extends AbstractVerticle {
    private static final Map<String, JsonObject> TODOS;

    static {
        TODOS = new HashMap<>();
        TODOS.put("abc123", new JsonObject()
                .put("id", "abc123")
                .put("todo", "something"));
    }

    @Override
    public void start() {
        vertx.eventBus().consumer("getTodo", this::getTodo);
        vertx.eventBus().consumer("createTodo", this::createTodo);
    }

    private void createTodo(Message<Object> objectMessage) {
        objectMessage.reply("todo created");
    }

    private void getTodo(Message<String> message) {
        String id = message.body();
        message.reply(TODOS.get(id));
    }
}
