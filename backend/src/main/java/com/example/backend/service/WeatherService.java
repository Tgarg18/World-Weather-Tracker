package com.example.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.example.backend.Response.WeatherResponse;
import com.example.backend.model.User;
import com.example.backend.model.WeatherEntity;
import com.example.backend.repository.WeatherRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

@Service
public class WeatherService {

    @Autowired
    private WeatherRepository weatherRepository;

    @Autowired
    private JavaMailSender mailSender;

    private static final Logger logger = Logger.getLogger(WeatherService.class.getName());
    private static FileHandler fileHandler;

    private final String API_KEY = "77cdec23b28f4f928b990534252701";
    private final String BASE_URL = "http://api.weatherapi.com/v1/current.json";

    static {
        try {
            fileHandler = new FileHandler("weather_service.txt", true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
            logger.setUseParentHandlers(false);
        } catch (IOException e) {
            System.err.println("Failed to initialize file logger: " + e.getMessage());
        }
    }

    @SuppressWarnings({ "null", "unchecked", "rawtypes" })
    public WeatherResponse getWeather(String city, User user) {
        String url = BASE_URL + "?key=" + API_KEY + "&q=" + city + "&aqi=no";
        RestTemplate restTemplate = new RestTemplate();

        logToFile(Level.INFO, "Incoming API request to fetch weather data for city: " + city);

        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            logToFile(Level.INFO,
                    "Received response from external weather API for city: " + city + " | Response: " + response);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> location = (Map<String, Object>) response.getBody().get("location");
                Map<String, Object> current = (Map<String, Object>) response.getBody().get("current");

                WeatherResponse weatherResponse = new WeatherResponse();
                weatherResponse.setCity((String) location.get("name"));
                weatherResponse.setTemperature((Double) current.get("temp_c"));
                weatherResponse
                        .setWeatherCondition((String) ((Map<String, Object>) current.get("condition")).get("text"));
                weatherResponse.setHumidity((Integer) current.get("humidity"));
                weatherResponse.setWindSpeed((Double) current.get("wind_kph"));
                weatherResponse.setTimestamp((String) location.get("localtime"));
                weatherResponse.setRegion((String) location.get("region"));
                weatherResponse.setCountry((String) location.get("country"));

                WeatherEntity weatherEntity = new WeatherEntity();
                weatherEntity.setCity(weatherResponse.getCity());
                weatherEntity.setTemperature(weatherResponse.getTemperature());
                weatherEntity.setWeatherCondition(weatherResponse.getWeatherCondition());
                weatherEntity.setHumidity(weatherResponse.getHumidity());
                weatherEntity.setWindSpeed(weatherResponse.getWindSpeed());
                weatherEntity.setTimestamp(LocalDateTime.now());
                weatherEntity.setRegion(weatherResponse.getRegion());
                weatherEntity.setCountry(weatherResponse.getCountry());

                weatherRepository.save(weatherEntity);

                checkAndSendAlert(weatherResponse, user);

                return weatherResponse;
            } else {
                throw new RuntimeException("Failed to fetch weather data");
            }

        } catch (HttpClientErrorException e) {
            logToFile(Level.SEVERE, "City not found: " + city + " | Error: " + e.getMessage());
            throw new RuntimeException("City not found", e);
        } catch (HttpServerErrorException e) {
            logToFile(Level.SEVERE, "Weather API is unavailable: " + e.getMessage());
            throw new RuntimeException("Weather API is unavailable", e);
        } catch (Exception e) {
            logToFile(Level.SEVERE,
                    "Error while fetching weather data for city: " + city + " | Error: " + e.getMessage());
            throw new RuntimeException("An unexpected error occurred", e);
        }
    }

    public List<WeatherResponse> getWeatherHistory(String city) {
        List<WeatherEntity> weatherEntities = weatherRepository.findTop5ByCityOrderByTimestampDesc(city);
        List<WeatherResponse> weatherResponses = new ArrayList<>();

        for (WeatherEntity entity : weatherEntities) {
            WeatherResponse response = new WeatherResponse();
            response.setCity(entity.getCity());
            response.setTemperature(entity.getTemperature());
            response.setWeatherCondition(entity.getWeatherCondition());
            response.setHumidity(entity.getHumidity());
            response.setWindSpeed(entity.getWindSpeed());
            response.setTimestamp(entity.getTimestamp().toString());
            weatherResponses.add(response);
        }

        logToFile(Level.INFO, "Fetched weather history for city: " + city);
        return weatherResponses;
    }

    private void checkAndSendAlert(WeatherResponse weatherResponse, User user) {
        if (weatherResponse.getTemperature() > 40 || weatherResponse.getTemperature() < 0) {
            String subject = "Extreme Weather Alert for " + weatherResponse.getCity() + "!";
            String body = "Temperature: " + weatherResponse.getTemperature() + "°C\n" +
                    "Condition: " + weatherResponse.getWeatherCondition() + "\n" +
                    "City: " + weatherResponse.getCity() + "\n" +
                    "Region: " + weatherResponse.getRegion() + "\n" +
                    "Country: " + weatherResponse.getCountry();

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(user.getEmail());
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
            logToFile(Level.WARNING, "Extreme weather alert sent for city: " + weatherResponse.getCity());
        }
    }

    private void logToFile(Level level, String message) {
        logger.log(level, message);
    }
}
