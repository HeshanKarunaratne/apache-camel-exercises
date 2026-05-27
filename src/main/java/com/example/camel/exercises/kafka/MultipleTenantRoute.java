package com.example.camel.exercises.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author Heshan Karunaratne
 */
@Component
@ConditionalOnExpression("${camel.exercises.multiple-tenant-kafka.enabled:true}")
public class MultipleTenantRoute extends RouteBuilder {
    @Value("${dl.kafka.region}")
    private String region;

    @Value("${dl.kafka.env}")
    private String env;

    @Value("${dl.kafka.domain}")
    private String domain;

    @Value("${dl.kafka.version}")
    private String version;

    @Value("${kafka.bootstrap.servers}")
    private String brokers;

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
                            region,
                            country,
                            env,
                            domain,
                            version
                    );

                    exchange.getIn().setHeader("kafka.TOPIC", topic);
                    exchange.getIn().setBody(body);
                    log.info("Publishing event to topic: {}", topic);
                })

                .toD("kafka:${header.kafka.TOPIC}?brokers=" + brokers);
    }
}
