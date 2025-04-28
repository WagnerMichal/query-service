package cz.muni.fi.pa053.query_service.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Service
public class AirportTemperatureService {

    private final RestTemplate restTemplate;

    public AirportTemperatureService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public double getTemperature(String airportCode) {
        String airportApiUrl = "http://airport-data.com/api/ap_info.json?iata=" + airportCode;
        Map<String, Object> airportData = restTemplate.getForObject(airportApiUrl, Map.class);

        if (airportData == null || airportData.get("latitude") == null || airportData.get("longitude") == null) {
            throw new IllegalArgumentException("Invalid airport code or missing location data.");
        }

        double latitude = Double.parseDouble(airportData.get("latitude").toString());
        double longitude = Double.parseDouble(airportData.get("longitude").toString());

        String weatherApiUrl = UriComponentsBuilder
                .fromHttpUrl("https://api.open-meteo.com/v1/forecast")
                .queryParam("latitude", latitude)
                .queryParam("longitude", longitude)
                .queryParam("current_weather", true)
                .toUriString();

        Map<String, Object> weatherResponse = restTemplate.getForObject(weatherApiUrl, Map.class);
        Map<String, Object> currentWeather = (Map<String, Object>) weatherResponse.get("current_weather");

        if (currentWeather == null || !currentWeather.containsKey("temperature")) {
            throw new IllegalArgumentException("Could not retrieve temperature.");
        }

        return Double.parseDouble(currentWeather.get("temperature").toString());
    }

}
