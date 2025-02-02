package com.example.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.backend.Response.WeatherResponse;
import com.example.backend.exception.UserException;
import com.example.backend.model.User;
import com.example.backend.service.UserService;
import com.example.backend.service.WeatherService;

import java.util.List;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    @Autowired
    private WeatherService weatherService;
    @Autowired
    private UserService userService;

    @GetMapping("/{city}")
    public ResponseEntity<WeatherResponse> getWeather(@RequestHeader("Authorization") String jwt,
            @PathVariable String city) throws UserException {
        User user = userService.findUserProfileByJwt(jwt);
        WeatherResponse weatherResponse = weatherService.getWeather(city,user);
        return ResponseEntity.ok(weatherResponse);
    }

    @GetMapping("/history/{city}")
    public ResponseEntity<List<WeatherResponse>> getWeatherHistory(@RequestHeader("Authorization") String jwt,
            @PathVariable String city) throws UserException {
        @SuppressWarnings("unused")
        User user = userService.findUserProfileByJwt(jwt);
        List<WeatherResponse> weatherHistory = weatherService.getWeatherHistory(city);
        return ResponseEntity.ok(weatherHistory);
    }
}
