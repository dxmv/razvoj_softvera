package org.raflab.studsluzbadesktopclient.services;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.model.dto.IspitDto;
import org.raflab.studsluzba.model.dto.IspitPrikazDto;
import org.raflab.studsluzba.model.dto.IspitniRokDto;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class IspitniRokService {

    private final WebClient webClient;
    private static final String ROKOVI_URL_PATH = "/api/ispitni-rokovi";

    public Flux<IspitniRokDto> getAllRokovi() {
        return webClient.get()
                .uri(ROKOVI_URL_PATH)
                .retrieve()
                .bodyToFlux(IspitniRokDto.class);
    }
    public Flux<IspitPrikazDto> getIspitiZaRok(Long rokId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/ispitni-rokovi/{id}/ispiti")
                        .build(rokId))
                .retrieve()
                .bodyToFlux(IspitPrikazDto.class);
    }
    public Mono<IspitniRokDto> saveRok(IspitniRokDto dto) {
        return webClient.post()
                .uri("/api/ispitni-rokovi/add")
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(IspitniRokDto.class);
    }
    public Flux<IspitDto> getIspitiZaRok2(Long rokId) {
        return webClient.get()
                .uri("/api/ispiti/rok/{rokId}", rokId)
                .retrieve()
                .bodyToFlux(IspitDto.class);
    }
}