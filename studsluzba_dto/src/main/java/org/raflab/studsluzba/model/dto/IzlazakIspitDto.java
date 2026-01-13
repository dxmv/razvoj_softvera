package org.raflab.studsluzba.model.dto;

import java.time.LocalDateTime;
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
public class IzlazakIspitDto {
    private Long id;
    private Long prijavaIspitaId;
    private Double poeniSaIspita;
    private Double ukupnoPoena;
    private String napomena;
    private Boolean ponistavanIspit;
    private LocalDateTime datumIzlaska;
}
