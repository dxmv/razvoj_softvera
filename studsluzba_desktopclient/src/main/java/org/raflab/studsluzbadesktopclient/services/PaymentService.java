package org.raflab.studsluzbadesktopclient.services;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.model.dto.RemainingTuitionDto;
import org.raflab.studsluzba.model.dto.UplataDto;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final WebClient webClient;

    public Flux<UplataDto> findPaymentsForStudent(Long studentId) {
        if (studentId == null) {
            return Flux.empty();
        }
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/uplate/student/{studentId}")
                        .build(studentId))
                .retrieve()
                .bodyToFlux(UplataDto.class);
    }

    public Mono<RemainingTuitionDto> getRemainingTuition(Long studentId) {
        if (studentId == null) {
            return Mono.empty();
        }
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/uplate/student/{studentId}/balance")
                        .build(studentId))
                .retrieve()
                .bodyToMono(RemainingTuitionDto.class);
    }
}
