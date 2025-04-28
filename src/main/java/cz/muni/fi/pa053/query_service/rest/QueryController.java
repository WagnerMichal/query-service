package cz.muni.fi.pa053.query_service.rest;

import cz.muni.fi.pa053.query_service.service.AirportTemperatureService;
import cz.muni.fi.pa053.query_service.service.ExpressionEvaluatorService;
import cz.muni.fi.pa053.query_service.service.StockPriceService;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlValue;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;
import java.util.stream.Stream;

@RestController
@RequestMapping("/")
public class QueryController {

    private final AirportTemperatureService airportTemperatureService;
    private final StockPriceService        stockPriceService;
    private final ExpressionEvaluatorService expressionEvaluatorService;

    public QueryController(AirportTemperatureService airportTemperatureService,
                           StockPriceService stockPriceService,
                           ExpressionEvaluatorService expressionEvaluatorService) {
        this.airportTemperatureService = airportTemperatureService;
        this.stockPriceService         = stockPriceService;
        this.expressionEvaluatorService = expressionEvaluatorService;
    }

    @GetMapping
    public ResponseEntity<?> query(
            @RequestParam(value = "queryAirportTemp", required = false) String airportCode,
            @RequestParam(value = "queryStockPrice", required = false) String stockSymbol,
            @RequestParam(value = "queryEval",        required = false) String expression,
            @RequestHeader(value = HttpHeaders.ACCEPT, defaultValue = MediaType.APPLICATION_JSON_VALUE)
            String acceptHeader) {

        long nonNull = Stream.of(airportCode, stockSymbol, expression)
                .filter(Objects::nonNull).count();
        if (nonNull != 1) {
            return ResponseEntity.badRequest().body("Exactly one query parameter must be present");
        }

        double result = airportCode != null ? airportTemperatureService.getTemperature(airportCode)
                : stockSymbol  != null ? stockPriceService.getStockPrice(stockSymbol)
                : expressionEvaluatorService.evaluate(expression);

        if (!acceptHeader.contains(MediaType.APPLICATION_XML_VALUE)) {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(result);
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_XML)
                .body(new ResultXml(result));
    }

    @XmlRootElement(name = "result")
    public static class ResultXml {
        @XmlValue
        private double value;
        public ResultXml() {}
        public ResultXml(double value) { this.value = value; }
    }
}
