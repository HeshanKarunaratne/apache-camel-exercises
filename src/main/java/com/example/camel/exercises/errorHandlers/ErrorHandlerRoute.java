package com.example.camel.exercises.errorHandlers;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author Heshan Karunaratne
 */
@Component
@ConditionalOnExpression("${camel.exercises.error-handler.enabled:false}")
public class ErrorHandlerRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        errorHandler(deadLetterChannel("direct:exceptionHandler").maximumRedeliveries(2));

        from("timer:time?period=1000")
                .process(exchange -> exchange.getIn().setBody(new Date()))
                .choice()
                .when(e -> CommonErrorHandlerRoute.COUNTER.incrementAndGet() % 2 == 0)
                .bean(HelloBean.class, "callBad")
                .otherwise()
                .bean(HelloBean.class, "callGood")
                .end()
                .log(LoggingLevel.INFO, ">> ${header.firedTime} >> ${body}")
                .to("log:reply");
    }
}