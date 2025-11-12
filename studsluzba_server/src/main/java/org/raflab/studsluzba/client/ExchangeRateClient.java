package org.raflab.studsluzba.client;

import org.raflab.studsluzba.model.dto.ExchangeRateDto;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@Component
public class ExchangeRateClient {

    private static final String TODAY_EUR_RATE_URL =
            "https://kurs.resenje.org/api/v1/currencies/eur/rates/today";

    private final RestTemplate restTemplate = new RestTemplate();

    public ExchangeRateDto fetchTodayEurRate() {
        try {
            ExchangeRateDto response = restTemplate.getForObject(
                    TODAY_EUR_RATE_URL, ExchangeRateDto.class);
            if (response == null) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                        "Prazan odgovor kursne liste");
            }
            return response;
        } catch (RestClientException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "Doslo je do greske");
        }
    }
}

