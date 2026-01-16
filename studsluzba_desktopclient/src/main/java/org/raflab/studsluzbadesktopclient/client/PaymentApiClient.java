package org.raflab.studsluzbadesktopclient.client;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.model.dto.CreateUplataRequest;
import org.raflab.studsluzba.model.dto.RemainingTuitionDto;
import org.raflab.studsluzba.model.dto.UplataDto;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * API Client for payment-related HTTP operations.
 * This class handles all HTTP communication with the server for payment endpoints.
 */
@Component
@RequiredArgsConstructor
public class PaymentApiClient {

    private final WebClient webClient;

    private static final String API_UPLATE_PATH = "/api/uplate";

    // ==================== ASYNC METHODS (WebClient - non-blocking) ====================

    /**
     * Find payments for student asynchronously (non-blocking).
     */
    public Flux<UplataDto> findPaymentsForStudentAsync(Long studentId) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(API_UPLATE_PATH + "/student/{studentId}")
                        .build(studentId))
                .retrieve()
                .bodyToFlux(UplataDto.class);
    }

    /**
     * Get remaining tuition balance asynchronously (non-blocking).
     */
    public Mono<RemainingTuitionDto> getRemainingTuitionAsync(Long studentId) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(API_UPLATE_PATH + "/student/{studentId}/balance")
                        .build(studentId))
                .retrieve()
                .bodyToMono(RemainingTuitionDto.class);
    }

    /**
     * Create payment asynchronously (non-blocking).
     */
    public Mono<UplataDto> createPaymentAsync(Long studentId, CreateUplataRequest request) {
        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(API_UPLATE_PATH + "/student/{studentId}")
                        .build(studentId))
                .bodyValue(request)
                .retrieve()
                .bodyToMono(UplataDto.class);
    }
}
