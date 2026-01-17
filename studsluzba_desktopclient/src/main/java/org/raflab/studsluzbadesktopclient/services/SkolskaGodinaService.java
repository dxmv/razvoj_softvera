package org.raflab.studsluzbadesktopclient.services;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.model.dto.SkolskaGodinaDto;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class SkolskaGodinaService {

    private final WebClient webClient;


    public Flux<SkolskaGodinaDto> getAll() {
        return webClient.get()
                .uri("/api/skolske-godine/all")
                .retrieve()
                .bodyToFlux(SkolskaGodinaDto.class);
    }
}