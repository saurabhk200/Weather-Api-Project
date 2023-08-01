package com.saurabhkumar.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class WeatherController {
    @Autowired
    private ConfigurableApplicationContext context;
    private static final String API_BASE_URL = "https://samples.openweathermap.org/data/2.5/forecast/hourly?q=London,us&appid=b6907d289e10d714a6e88b30761fae22";

    @GetMapping("/")
    public String showForm() {
        return "weather-form";
    }

    @PostMapping("/getWeather")
    public String getWeather(@RequestParam(name = "weatherDate") String date, Model model) throws IOException {
        String apiUrl = buildApiUrl();
        String response = getApiResponse(apiUrl);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response);
        String temperature = getTemperatureForDate(jsonNode, date);

        model.addAttribute("result", "Temperature on " + date + ": " + temperature);
        return "weather-result";
    }

    @PostMapping("/getWindSpeed")
    public String getWindSpeed(@RequestParam(name = "windSpeedDate") String date, Model model) throws IOException {
        String apiUrl = buildApiUrl();
        String response = getApiResponse(apiUrl);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response);

        String windSpeed = getWindSpeedForDate(jsonNode, date);

        model.addAttribute("result", "Wind Speed on " + date + ": " + windSpeed);
        return "weather-result";
    }

    @PostMapping("/getPressure")
    public String getPressure(@RequestParam(name = "pressureDate") String date, Model model) throws IOException {
        String apiUrl = buildApiUrl();
        String response = getApiResponse(apiUrl);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response);
        String pressure = getPressureForDate(jsonNode, date);

        model.addAttribute("result", "Pressure on " + date + ": " + pressure);
        return "weather-result";
    }

    @RequestMapping("/exit")
    public String exit() {
        context.close();
        return "exit-form";
    }

    private String buildApiUrl() {
        return API_BASE_URL;
    }

    private String getApiResponse(String apiUrl) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(apiUrl, String.class);
    }

    private String getTemperatureForDate(JsonNode jsonNode, String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (JsonNode forecast : jsonNode.get("list")) {
            long forecastTimestamp = forecast.get("dt").asLong() * 1000;
            Date forecastDate = new Date(forecastTimestamp);
            if (sdf.format(forecastDate).startsWith(date)) {
                double temperature = forecast.get("main").get("temp").asDouble();
                return temperature + "°C";
            }
        }
        return "Data not available for the given date.";
    }

    private String getWindSpeedForDate(JsonNode jsonNode, String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (JsonNode forecast : jsonNode.get("list")) {
            long forecastTimestamp = forecast.get("dt").asLong() * 1000;
            Date forecastDate = new Date(forecastTimestamp);
            if (sdf.format(forecastDate).startsWith(date)) {
                double windSpeed = forecast.get("wind").get("speed").asDouble();
                return windSpeed + " m/s";
            }
        }
        return "Data not available for the given date.";
    }

    private String getPressureForDate(JsonNode jsonNode, String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (JsonNode forecast : jsonNode.get("list")) {
            long forecastTimestamp = forecast.get("dt").asLong() * 1000;
            Date forecastDate = new Date(forecastTimestamp);
            if (sdf.format(forecastDate).startsWith(date)) {
                double pressure = forecast.get("main").get("pressure").asDouble();
                return pressure + " hPa";
            }
        }
        return "Data not available for the given date.";
    }
}
