package com.mtb.app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${rest.client.connect-timeout-millis:3000}")
    private Integer connectTimeoutMillis;

    @Value("${rest.client.read-timeout-millis:10000}")
    private Integer readTimeoutMillis;

    @Bean
    public RestClient restClient() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(connectTimeoutMillis);
        requestFactory.setReadTimeout(readTimeoutMillis);

        return RestClient.builder()
                .requestFactory(requestFactory)
                .build();
    }
}
