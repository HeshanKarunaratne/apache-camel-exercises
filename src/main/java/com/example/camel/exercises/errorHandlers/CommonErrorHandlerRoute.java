package com.example.camel.exercises.errorHandlers;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Heshan Karunaratne
 */
@Component
@ConditionalOnExpression("${camel.exercises.common-error-handler.enabled:true}")
public class CommonErrorHandlerRoute extends RouteBuilder {

    public static final AtomicInteger COUNTER = new AtomicInteger(1);

    @Override
    public void configure() throws Exception {
        from("direct:exceptionHandler")
                .log(LoggingLevel.WARN, "In Exception Handler")
                .log(LoggingLevel.WARN, "${body}");
    }
}