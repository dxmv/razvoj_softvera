package org.raflab.studsluzbadesktopclient.services;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.model.dto.PredmetDto;
import org.raflab.studsluzbadesktopclient.client.PredmetApiClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Service layer for predmet (subject) related business logic.
 * Delegates HTTP operations to PredmetApiClient.
 * Contains validation, business rules, and orchestration.
 */
@Service
@RequiredArgsConstructor
public class PredmetService {

    private final PredmetApiClient predmetApiClient;

    /**
     * Find predmet by ID asynchronously.
     */
    public Mono<PredmetDto> findPredmetById(Long id) {
        if (id == null) {
            return Mono.empty();
        }
        return predmetApiClient.findPredmetByIdAsync(id);
    }

    /**
     * Find predmeti by multiple IDs asynchronously.
     */
    public Flux<PredmetDto> findPredmetsByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Flux.empty();
        }
        return predmetApiClient.findPredmetsByIdsAsync(ids);
    }

    /**
     * Find all predmeti asynchronously.
     */
    public Flux<PredmetDto> findAllPredmeti() {
        return predmetApiClient.findAllPredmetiAsync();
    }

    /**
     * Get average grade for predmet in year range asynchronously.
     */
    public Mono<Double> getAverageGrade(Long predmetId, int yearFrom, int yearTo) {
        if (predmetId == null) {
            return Mono.empty();
        }
        return predmetApiClient.getAverageGradeAsync(predmetId, yearFrom, yearTo);
    }
}
