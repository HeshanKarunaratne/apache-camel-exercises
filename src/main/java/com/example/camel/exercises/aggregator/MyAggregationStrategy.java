package com.example.camel.exercises.aggregator;

import org.apache.camel.Exchange;

import java.util.ArrayList;

/**
 * @author Heshan Karunaratne
 */
public class MyAggregationStrategy implements org.apache.camel.AggregationStrategy {
    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {

        OrderItem newItem = newExchange.getMessage().getBody(OrderItem.class);

        if (oldExchange == null) {
            Order order = new Order(newItem.getOrderId(), new ArrayList<>());
            order.getItems().add(newItem.getItem());

            newExchange.getMessage().setBody(order);
            return newExchange;
        }

        Order order = oldExchange.getMessage().getBody(Order.class);
        order.getItems().add(newItem.getItem());

        return oldExchange;
    }
}