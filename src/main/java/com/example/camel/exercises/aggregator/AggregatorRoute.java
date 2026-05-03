package com.example.camel.exercises.aggregator;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * @author Heshan Karunaratne
 */
@Component
@ConditionalOnExpression("${camel.exercises.aggregator.enabled:false}")
public class AggregatorRoute extends RouteBuilder {
    private static final String ORDER_ID = "orderId";
    private final Random random = new Random();

    @Override
    public void configure() {

        // Simulate incoming order items
        from("timer:orders?period=1000")
                .routeId("order-producer")

                .process(exchange -> {
                    int orderId = 1000 + random.nextInt(3);
                    String item = "Item-" + (char) ('A' + random.nextInt(5));

                    exchange.getMessage().setHeader(ORDER_ID, orderId);
                    exchange.getMessage().setBody(new OrderItem(orderId, item));
                })

                .log("Incoming -> Order: ${header.orderId}, Item: ${body.item}")

                // Aggregate by orderId
                .aggregate(header(ORDER_ID), new MyAggregationStrategy())
                .completionSize(3)// assume max 3 items per order
                .log("COMPLETED ORDER -> ${body}");
    }
}