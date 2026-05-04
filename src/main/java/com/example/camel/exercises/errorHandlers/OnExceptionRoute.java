package com.example.camel.exercises.errorHandlers;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Heshan Karunaratne
 */
@Component
@ConditionalOnExpression("${camel.exercises.on-exception.enabled:false}")
public class OnExceptionRoute extends RouteBuilder {

    static final AtomicInteger counter = new AtomicInteger(1);

    @Override
    public void configure() throws Exception {
        onException(Exception.class)
                .log(LoggingLevel.ERROR, "JSS: ${exception}")
                .handled(true)
                .to("direct:exceptionHandler");

        from("timer:time?period=1000")
                .process(exchange -> exchange.getIn().setBody(new Date()))
                .choice()
                .when(e -> counter.incrementAndGet() % 2 == 0)
                .bean(HelloBean.class, "callBad")
                .otherwise()
                .bean(HelloBean.class, "callGood")
                .end()
                .log(LoggingLevel.INFO, ">> ${header.firedTime} >> ${body}")
                .to("log:reply");
    }
}