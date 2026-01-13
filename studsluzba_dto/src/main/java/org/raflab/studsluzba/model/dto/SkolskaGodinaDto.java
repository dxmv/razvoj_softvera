package org.raflab.studsluzba.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkolskaGodinaDto {
    private Long id;
    private String oznaka;
    private LocalDate datumPocetka;
    private LocalDate datumZavrsetka;
    private Boolean aktivna;
}
