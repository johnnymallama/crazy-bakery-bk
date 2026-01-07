package uan.edu.co.crazy_bakery.infrastructure.web.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${colombia.api.base-url}")
    private String colombiaApiBaseUrl;

    @Bean
    public RestClient colombiaApiRestClient() {
        return RestClient.builder()
                .baseUrl(colombiaApiBaseUrl)
                .build();
    }
}
