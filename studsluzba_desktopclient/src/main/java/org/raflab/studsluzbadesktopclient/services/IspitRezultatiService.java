package org.raflab.studsluzbadesktopclient.services;

import lombok.AllArgsConstructor;
import org.raflab.studsluzba.model.dto.IspitniRezultatDto;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Service
@AllArgsConstructor
public class IspitRezultatiService {

    private final WebClient webClient;



    public Flux<IspitniRezultatDto> getRezultati(Long ispitId) {
        return webClient.get()
                .uri("/api/izlasci-na-ispit/rezultati/{ispitId}", ispitId)
                .retrieve()
                .bodyToFlux(IspitniRezultatDto.class);
    }
}