package com.example.camel.exercises.contentBasedRoutine;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

/**
 * @author Heshan Karunaratne
 */
@Component
@ConditionalOnExpression("${camel.exercises.choice-route.enabled:false}")
public class ChoiceRoute extends RouteBuilder {

    public static final String WIDGET = "widget";
    public static final String GADGET = "gadget";
    public static final String GENERAL = "general";
    public static final String INVENTORY = "inventory";
    private static final String HEADER_INVENTORY = "${header." + INVENTORY + "}";

    @Override
    public void configure() throws Exception {
        from("direct:orders")
                .choice()
                .when(simple(HEADER_INVENTORY + " == '" + WIDGET + "'"))
                .to("direct:widget")
                .when(simple(HEADER_INVENTORY + " == '" + GADGET + "'"))
                .to("direct:gadget")
                .otherwise()
                .to("direct:general");

        from("direct:widget").routeId(WIDGET).log(LoggingLevel.ERROR, "Got a " + WIDGET + "order for ${body}");

        from("direct:gadget").routeId(GADGET).log(LoggingLevel.ERROR, "Got a " + GADGET + " order for ${body}");

        from("direct:general")
                .routeId(GENERAL)
                .log(LoggingLevel.ERROR, "Got a " + GENERAL + " order for ${body}");
    }
}
