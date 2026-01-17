package org.raflab.studsluzbadesktopclient.services;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.model.dto.PredmetDto;
import org.raflab.studsluzba.model.dto.StudProgramDto;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class PredmetService {
    private static final String PREDMETI_BASE_PATH = "/api/predmeti";

    private final WebClient webClient;

    public Mono<List<PredmetDto>> getPredmetiByProgram(Long programId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/predmeti/by-stud-program")
                        .queryParam("studProgramId", programId).build())
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(node -> {
                    List<PredmetDto> list = new ArrayList<>();
                    JsonNode content = node.get("content");
                    if (content != null && content.isArray()) {
                        content.forEach(p -> list.add(PredmetDto.builder()
                                .id(p.get("id").asLong())
                                .sifra(p.get("sifra").asText())
                                .naziv(p.get("naziv").asText())
                                .espbBodovi(p.get("espbBodovi").asInt())
                                .semestar(p.get("semestar").asInt())
                                .build()));
                    }
                    return list;
                });
    }

    public Mono<Double> getAverageOcena(Long predmetId, String gOd, String gDo) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/polozeni-predmeti/averageOcena")
                        .queryParam("predmetId", predmetId)
                        .queryParam("godinaOd", gOd)
                        .queryParam("godinaDo", gDo).build())
                .retrieve()
                .bodyToMono(Double.class);
    }

    public Mono<Void> savePredmet(PredmetDto dto) {
        return webClient.post()
                .uri("/api/predmeti/create")
                .bodyValue(dto)
                .retrieve()
                .toBodilessEntity()
                .then(); // Pretvara Mono<ResponseEntity> u Mono<Void>
    }

    public Flux<StudProgramDto> getAllStudijskiProgrami() {
        return webClient.get()
                .uri("/api/stud-programi")
                .retrieve()
                .bodyToFlux(StudProgramDto.class);
    }

    public Flux<PredmetDto> getAllPredmeti() {
        return webClient.get()
                .uri(PREDMETI_BASE_PATH + "/all")
                .retrieve()
                .bodyToFlux(PredmetDto.class);
    }

    public Flux<PredmetDto> findPredmetsByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Flux.empty();
        }
        return webClient.post()
                .uri(PREDMETI_BASE_PATH + "/by-ids")
                .bodyValue(ids)
                .retrieve()
                .bodyToFlux(PredmetDto.class);
    }
}
