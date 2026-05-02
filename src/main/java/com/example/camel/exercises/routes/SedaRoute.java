package com.example.camel.exercises.routes;

import org.apache.camel.LoggingLevel;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.support.DefaultMessage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author Heshan Karunaratne
 */
@Component
@ConditionalOnExpression("${camel.exercises.seda-route.enabled:false}")
public class SedaRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("timer:ping?period=200")
                .process(exchange -> {
                    Message message = new DefaultMessage(exchange);
                    message.setBody(new Date());
                    exchange.setMessage(message);
                })
                .to("seda:weightLifter?multipleConsumers=true");

        from("seda:weightLifter?multipleConsumers=true")
                .to("direct:complexProcess");

        from("direct:complexProcess")
                .log(LoggingLevel.INFO, "${body}")
                .process(exchange -> TimeUnit.SECONDS.sleep(2))
                .end();
    }
}
