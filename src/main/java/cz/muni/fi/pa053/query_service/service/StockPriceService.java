package cz.muni.fi.pa053.query_service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class StockPriceService {

    private final RestTemplate restTemplate;
    private final String apiKey;

    public StockPriceService(RestTemplate restTemplate, @Value("${stock.api-key}") String apiKey) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
    }

    public double getStockPrice(String symbol) {
        String url = "https://api.twelvedata.com/price?symbol=" + symbol + "&apikey=" + apiKey;
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        if (response == null || !response.containsKey("price")) {
            throw new IllegalArgumentException("Could not retrieve stock price.");
        }

        return Double.parseDouble(response.get("price").toString());
    }
}
