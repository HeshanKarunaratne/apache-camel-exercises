package com.example.camel.exercises.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author Heshan Karunaratne
 */
@Component
@ConditionalOnExpression("${camel.exercises.multiple-tenant-kafka.enabled:false}")
public class MultipleTenantRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        // REST entry point
        rest("/publish-event")
                .post()
                .consumes("application/json")
                .to("direct:publish-event");


        from("direct:publish-event")
                .routeId("dynamic-kafka-publisher")

                .process(exchange -> {
                    String body = exchange.getIn().getBody(String.class);

                    if (body == null || body.isBlank()) {
                        throw new IllegalArgumentException("Request body is empty");
                    }

                    ObjectMapper mapper = new ObjectMapper();
                    Map<String, Object> json = mapper.readValue(body, Map.class);
                    String country = ((String) json.get("country")).toLowerCase();
                    String topic = String.format(
                            "%s.%s.%s.%s.%s",
                            "us-east-1",
                            country.toLowerCase(),
                            "prod",
                            "fo-be",
                            "v1"
                    );

                    exchange.getIn().setHeader("kafka.TOPIC", topic);
                    exchange.getIn().setBody("test body");
                    log.info("Publishing event to topic: {}", topic);
                })

                .toD("kafka:${header.kafka.TOPIC}?brokers=localhost:9092");
    }
}
