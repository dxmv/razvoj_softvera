package org.raflab.studsluzbadesktopclient.services;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.model.dto.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class IspitService {

    private final WebClient webClient;

    private String baseUrl;

    private final String ISPITI_URL_PATH = "/api/ispiti";
    private final String NASTAVNIK_URL_PATH = "/api/nastavnik-predmeti";

    private String createURL(String basePath, String pathEnd) {
        return basePath + "/" + pathEnd;
    }


    public Mono<Long> getNastavnikIdByPredmet(Long predmetId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/nastavnik-predmeti/by-predmet")
                        .queryParam("predmetId", predmetId)
                        .build())
                .retrieve()
                .bodyToMono(Long.class);
    }


    public Mono<IspitDto> saveIspit(IspitDto dto) {
        return webClient.post()
                .uri(ISPITI_URL_PATH)
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(IspitDto.class);
    }
    public Flux<PrijavaIspitaPrikazDto> getPrijavljeniStudenti(Long ispitId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/prijave-ispita/{ispitId}/prijavljeni2")
                        .build(ispitId))
                .retrieve()
                .bodyToFlux(PrijavaIspitaPrikazDto.class);
    }

}