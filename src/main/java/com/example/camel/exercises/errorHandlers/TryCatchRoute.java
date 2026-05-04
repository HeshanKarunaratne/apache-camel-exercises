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
@ConditionalOnExpression("${camel.exercises.try-catch.enabled:false}")
public class TryCatchRoute extends RouteBuilder {

    static final AtomicInteger counter = new AtomicInteger(1);

    @Override
    public void configure() throws Exception {
        from("timer:time?period=1000")
                .process(exchange -> exchange.getIn().setBody(new Date()))
                .doTry()
                .bean(HelloBean.class, "callBad")
                .doCatch(Exception.class)
                .to("direct:exceptionHandler")
                .end()
                .log(LoggingLevel.INFO, ">> ${header.firedTime} >> ${body}")
                .to("log:reply");
    }
}