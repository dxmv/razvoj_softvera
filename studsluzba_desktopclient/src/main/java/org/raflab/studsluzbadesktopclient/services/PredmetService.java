package org.raflab.studsluzbadesktopclient.services;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.model.dto.PredmetDto;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PredmetService {

    private final WebClient webClient;

    public Mono<PredmetDto> findPredmetById(Long id) {
        if (id == null) {
            return Mono.empty();
        }
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/predmeti/{id}")
                        .build(id))
                .retrieve()
                .bodyToMono(PredmetDto.class);
    }

    public Flux<PredmetDto> findPredmetsByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Flux.empty();
        }
        return Flux.fromIterable(ids)
                .flatMap(this::findPredmetById);
    }
}
