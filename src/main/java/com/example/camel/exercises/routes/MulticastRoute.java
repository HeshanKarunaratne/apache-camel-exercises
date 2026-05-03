package com.example.camel.exercises.routes;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Heshan Karunaratne
 */
@Component
@ConditionalOnExpression("${camel.exercises.multicast-route.enabled:true}")
public class MulticastRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        AtomicInteger orderId = new AtomicInteger(100);

        from("timer:orders?period=2000")
                .process(
                        exchange ->
                                exchange.getIn()
                                        .setBody(
                                                "{order-id: '"
                                                        + (orderId.getAndIncrement())
                                                        + "', "
                                                        + "price: '£20.00'}"))
                .multicast()
                .parallelProcessing()
                .to("direct:payment", "direct:stock-allocation");

        from("direct:payment").process(exchange -> enrich(exchange, "Paid")).log(LoggingLevel.INFO, "${body}");
        from("direct:stock-allocation")
                .process(exchange -> enrich(exchange, "Allocated"))
                .log(LoggingLevel.INFO, "${body}");
    }

    private void enrich(Exchange exchange, String statusValue) {
        Message in = exchange.getIn();
        String order = in.getBody(String.class);
        String status = "'status': '" + statusValue + "'";
        String body = order.replace("}", ", " + status + "}");
        in.setBody(body);
    }
}