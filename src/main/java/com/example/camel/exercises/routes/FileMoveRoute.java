package com.example.camel.exercises.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * @author Heshan Karunaratne
 */
@Component
public class FileMoveRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("file://tmp/camel")
                .log("Headers -> ${headers}")
                .log("Body -> ${body}")
                .to("file://tmp/camel/processed");
    }
}
