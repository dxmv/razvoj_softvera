package org.raflab.studsluzbadesktopclient.client;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.model.dto.PredmetDto;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * API Client for predmet (subject) related HTTP operations.
 * This class handles all HTTP communication with the server for predmet endpoints.
 */
@Component
@RequiredArgsConstructor
public class PredmetApiClient {

    private final WebClient webClient;

    private static final String API_PREDMETI_PATH = "/api/predmeti";

    // ==================== ASYNC METHODS (WebClient - non-blocking) ====================

    /**
     * Find predmet by ID asynchronously (non-blocking).
     */
    public Mono<PredmetDto> findPredmetByIdAsync(Long id) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(API_PREDMETI_PATH + "/{id}")
                        .build(id))
                .retrieve()
                .bodyToMono(PredmetDto.class);
    }

    /**
     * Find all predmeti asynchronously (non-blocking).
     */
    public Flux<PredmetDto> findAllPredmetiAsync() {
        return webClient
                .get()
                .uri(API_PREDMETI_PATH)
                .retrieve()
                .bodyToFlux(PredmetDto.class);
    }

    /**
     * Find predmeti by multiple IDs asynchronously (non-blocking).
     * Makes parallel requests for each ID.
     */
    public Flux<PredmetDto> findPredmetsByIdsAsync(List<Long> ids) {
        return Flux.fromIterable(ids)
                .flatMap(this::findPredmetByIdAsync);
    }

    /**
     * Get average grade for predmet in year range asynchronously (non-blocking).
     */
    public Mono<Double> getAverageGradeAsync(Long predmetId, int yearFrom, int yearTo) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(API_PREDMETI_PATH + "/{id}/avg")
                        .queryParam("yearFrom", yearFrom)
                        .queryParam("yearTo", yearTo)
                        .build(predmetId))
                .retrieve()
                .bodyToMono(Double.class);
    }
}
