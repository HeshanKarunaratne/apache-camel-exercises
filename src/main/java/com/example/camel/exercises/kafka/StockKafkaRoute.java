package com.example.camel.exercises.kafka;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author Heshan Karunaratne
 */
@Component
@ConditionalOnExpression("${camel.exercises.stock-kafka.enabled:false}")
public class StockKafkaRoute extends RouteBuilder {

    final String KAFKA_ENDPOINT = "kafka:%s?brokers=localhost:9092";

    @Override
    public void configure() throws Exception {
        fromF(KAFKA_ENDPOINT, "stock-live")
                .log(LoggingLevel.INFO, "[${header.kafka.OFFSET}] [${body}]")
                .bean(StockPriceEnricher.class)
                .toF(KAFKA_ENDPOINT, "stock-audit");
    }

    private class StockPriceEnricher {
        public String enrichStockPrice(String stockPrice) {
            return stockPrice + "," + new Date();
        }
    }
}