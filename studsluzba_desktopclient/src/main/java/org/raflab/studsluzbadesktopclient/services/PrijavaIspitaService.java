package org.raflab.studsluzbadesktopclient.services;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.model.dto.PrijavaIspitaDto;
import org.raflab.studsluzba.model.dto.StudentDto;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PrijavaIspitaService {

    private final WebClient webClient;


    public Mono<PrijavaIspitaDto> prijaviIspit(Long ispitId, Long indeksId) {
        PrijavaIspitaDto dto = new PrijavaIspitaDto();
        dto.setIspitId(ispitId);
        dto.setStudentskiIndeksId(indeksId);
        dto.setDatumPrijave(LocalDateTime.now());

        return webClient.post()
                .uri("/api/prijave-ispita/prijavi")
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(PrijavaIspitaDto.class);
    }


    public Mono<StudentDto> getStudentByIndeks(Long id) {
        return webClient.get()
                .uri("/api/studenti/find/{id}", id)
                .retrieve()
                .bodyToMono(StudentDto.class);
    }
    public Mono<Long> fetchIndeksId(Long studentId) {
        return webClient.get()
                .uri("/api/indeksi/student/{id}/aktivan", studentId)
                .retrieve()
                .bodyToMono(Long.class);
    }
}