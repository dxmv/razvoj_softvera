package org.raflab.studsluzbadesktopclient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ClientAppConfig {
	@Bean
    public RestTemplate getRestTemplate() {
       return new RestTemplate();
    }

    @Bean
    public WebClient getWebClient() {
        return WebClient.builder()
                .baseUrl(getBaseUrl())
                .build();
    }
    
	@Bean
    public String getBaseUrl() {
       String override = System.getenv("STUDSLUZBA_BASE_URL");
       return (override == null || override.isBlank()) ? "http://localhost:8081" : override.trim();
    }
}
