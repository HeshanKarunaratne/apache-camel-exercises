package com.example.camel.exercises.kafka;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

/**
 * @author Heshan Karunaratne
 */
@Component
@ConditionalOnExpression("${camel.exercises.multiple-tenant-consumer-kafka.enabled:true}")
public class MultiTenantConsumerRoute extends RouteBuilder {
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
        String topicPattern =
                region + "\\.[a-z]{1,3}\\." + env + "\\." + domain + "\\." + version;

        from("kafka:" + topicPattern
                + "?brokers=" + brokers
                + "&groupId=dl-multi-tenant-consumer"
                + "&autoOffsetReset=earliest"
                + "&consumersCount=1"
                + "&topicIsPattern=true"
                + "&metadataMaxAgeMs=5000"
        )
                .routeId("dl-multi-tenant-consumer-route")

                .log("Received from topic ${headers[kafka.TOPIC]} : ${body}")

                .process(exchange -> {
                    String topic = exchange.getIn().getHeader("kafka.TOPIC", String.class);
                    String[] parts = topic.split("\\.");

                    if (parts.length < 5) throw new IllegalArgumentException("Invalid topic format: " + topic);

                    String tenant = parts[1];
                    exchange.setProperty("tenant", tenant);
                    log.info("Tenant identified: {}", tenant);
                });
    }

}
