package com.example.camel.exercises.testContainers;

import com.example.camel.exercises.ExercisesApplication;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

/**
 * @author Heshan Karunaratne
 */
@Testcontainers
@SpringBootTest(classes = ExercisesApplication.class)
@CamelSpringBootTest
@Slf4j
class WeatherRouteTestcontainersTest {
    public static String RMQ_HOST = "rmq.host";
    public static String RMQ_PORT = "rmq.port";
    public static final String EXCHANGE_WEATHER_DATA = "weather.data";
    public static final String QUEUE_WEATHER_EVENTS = "weather-events";
    public static final String QUEUE_WEATHER_DATA = "weather-data";
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Container
    public static RabbitMQContainer rabbitMQContainer =
            new RabbitMQContainer(DockerImageName.parse("rabbitmq:3.13-management"));

    @DynamicPropertySource
    static void registerRabbitProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.rabbitmq.host", rabbitMQContainer::getHost);
        registry.add("spring.rabbitmq.port", rabbitMQContainer::getAmqpPort);
        registry.add("spring.rabbitmq.username", rabbitMQContainer::getAdminUsername);
        registry.add("spring.rabbitmq.password", rabbitMQContainer::getAdminPassword);
    }

    @AfterAll
    static void cleanup() {
        rabbitMQContainer.stop();
    }

    @SneakyThrows
    @Test
    void sendAndReceiveMessage() {

        rabbitTemplate.send(EXCHANGE_WEATHER_DATA, QUEUE_WEATHER_DATA, message());

        Message response = rabbitTemplate.receive(QUEUE_WEATHER_EVENTS, 3000);

        Assertions.assertNotNull(response, "Response must be non-null");

        String body = new String(response.getBody());

        Assertions.assertTrue(body.contains("id"), "Id must be defined");
        Assertions.assertTrue(body.contains("receivedTime"), "receivedTime must be defined");
    }

    private Message message() {
        return MessageBuilder.withBody(
                "{ \"city\": \"London\", \"temp\": \"20\", \"unit\": \"C\"}".getBytes()
        ).build();
    }


}