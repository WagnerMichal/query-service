package cz.muni.fi.pa053.query_service.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        MappingJackson2HttpMessageConverter jackson =
                new MappingJackson2HttpMessageConverter();
        jackson.setSupportedMediaTypes(
                List.of(MediaType.APPLICATION_JSON, MediaType.TEXT_HTML));

        return builder.additionalMessageConverters(jackson).build();
    }
}
