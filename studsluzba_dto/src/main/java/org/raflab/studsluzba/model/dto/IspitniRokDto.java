package org.raflab.studsluzba.model.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IspitniRokDto {
    private Long id;
    private LocalDate datumPocetka;
    private LocalDate datumZavrsetka;
    private Long skolskaGodinaId;
}
