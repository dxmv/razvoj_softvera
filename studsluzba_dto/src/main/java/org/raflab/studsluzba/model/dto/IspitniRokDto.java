package org.raflab.studsluzba.model.dto;

import java.time.LocalDate;

import lombok.*;

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

    @Override
    public String toString() {
        return " od: "+datumPocetka.toString()+" do: " + datumZavrsetka.toString();
    }
}
