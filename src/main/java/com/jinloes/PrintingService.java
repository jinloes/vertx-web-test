package com.jinloes;

import io.vertx.core.AbstractVerticle;

import java.lang.management.ManagementFactory;
import java.util.concurrent.TimeUnit;

/**
 * Created by rr2re on 9/3/2015.
 */
public class PrintingService extends AbstractVerticle {

    @Override
    public void start() {
        vertx.setPeriodic(TimeUnit.SECONDS.toMillis(2), event -> {
            final String name = ManagementFactory.getRuntimeMXBean().getName();
            System.out.println("Firing on " + name);
        });
    }

}
