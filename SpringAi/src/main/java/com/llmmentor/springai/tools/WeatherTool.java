package com.llmmentor.springai.tools;

import com.llmmentor.springai.service.WeatherService;
import jakarta.annotation.Resource;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

@Component
public class WeatherTool {

    @Resource
    private WeatherService weatherService;

    @Tool(name = "get_weather", description = "Get the current weather")
    public String getWeather() {
        return weatherService.getWeather();
    }
}
