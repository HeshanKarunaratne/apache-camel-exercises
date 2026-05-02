package com.example.camel.exercises.provider;

import com.example.camel.exercises.dto.WeatherDto;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Heshan Karunaratne
 */
@Component
public class WeatherDataProvider {
    private static Map<String, WeatherDto> weatherData = new HashMap<>();

    public WeatherDataProvider() {
        WeatherDto dto = WeatherDto.builder()
                .city("London")
                .temp("30.0")
                .unit("C")
                .receivedTime(new Date().toString()).id(1).build();
        weatherData.put("LONDON", dto);
    }

    public WeatherDto getCurrentWeather(String city) {
        return weatherData.get(city.toUpperCase());
    }

    public void setCurrentWeather(WeatherDto dto) {
        dto.setReceivedTime(new Date().toString());
        weatherData.put(dto.getCity().toUpperCase(), dto);
    }

}
