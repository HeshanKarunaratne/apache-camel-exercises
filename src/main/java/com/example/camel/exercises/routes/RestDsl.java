package com.example.camel.exercises.routes;

import com.example.camel.exercises.dto.WeatherDto;
import com.example.camel.exercises.provider.WeatherDataProvider;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * @author Heshan Karunaratne
 */
@Component
@RequiredArgsConstructor
@ConditionalOnExpression("${camel.exercises.rest-dsl.enabled:false}")
public class RestDsl extends RouteBuilder {

    private final WeatherDataProvider weatherDataProvider;

    @Override
    public void configure() {
        restConfiguration()
                .component("platform-http")
                .bindingMode(RestBindingMode.json);
        rest()
                .get("/weather/{city}").outType(WeatherDto.class).to("direct:getWeather")
                .post("/weather").type(WeatherDto.class).to("direct:saveWeather");

        from("direct:getWeather").process(this::getWeatherData);
        from("direct:saveWeather").process(this::saveWeatherData).wireTap("direct:write-to-rabbit");

        from("direct:write-to-rabbit")
                .marshal().json(JsonLibrary.Jackson, WeatherDto.class)
                .toF("rabbitmq:weather-exchange"
                        + "?hostname=localhost"
                        + "&portNumber=5672"
                        + "&username=admin"
                        + "&password=admin"
                        + "&queue=weather-events"
                        + "&autoDelete=false", "");

    }

    private void getWeatherData(Exchange exchange) {
        String city = exchange.getMessage().getHeader("city", String.class);
        WeatherDto weather = weatherDataProvider.getCurrentWeather(city);

        if (weather != null) {
            exchange.getMessage().setBody(weather);
        } else {
            exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, HttpStatus.NOT_FOUND.value());
            exchange.getMessage().setBody("City not found");
        }
    }

    private void saveWeatherData(Exchange exchange) {
        WeatherDto dto = exchange.getMessage().getBody(WeatherDto.class);
        weatherDataProvider.setCurrentWeather(dto);
    }
}
