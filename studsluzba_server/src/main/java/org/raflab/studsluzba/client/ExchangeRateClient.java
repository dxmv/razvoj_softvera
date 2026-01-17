package org.raflab.studsluzba.client;


import org.raflab.studsluzba.model.dto.ExchangeRateDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class ExchangeRateClient {

    private static final Logger log = LoggerFactory.getLogger(ExchangeRateClient.class);

    private static final String TODAY_EUR_RATE_URL =
            "https://kurs.resenje.org/api/v1/currencies/eur/rates/today";
    private static final BigDecimal DEFAULT_MIDDLE_RATE = new BigDecimal("117.5");

    private final RestTemplate restTemplate = new RestTemplate();

    public ExchangeRateDto fetchTodayEurRate() {
        try {
            ExchangeRateDto response = restTemplate.getForObject(
                    TODAY_EUR_RATE_URL, ExchangeRateDto.class);
            if (response == null || response.getExchangeMiddle() == null) {
                log.warn("Prazan odgovor kursne liste, koristi se podrazumevani kurs {}", DEFAULT_MIDDLE_RATE);
                return fallbackRate();
            }
            return response;
        } catch (RestClientException ex) {
            log.warn("Neuspešno preuzimanje kursne liste: {}. Koristi se podrazumevani kurs {}", ex.getMessage(), DEFAULT_MIDDLE_RATE);
            return fallbackRate();
        }
    }

    private ExchangeRateDto fallbackRate() {
        ExchangeRateDto dto = new ExchangeRateDto();
        dto.setCode("EUR");
        dto.setDate(LocalDate.now());
        dto.setExchangeMiddle(DEFAULT_MIDDLE_RATE);
        dto.setExchangeBuy(DEFAULT_MIDDLE_RATE);
        dto.setExchangeSell(DEFAULT_MIDDLE_RATE);
        return dto;
    }
}
