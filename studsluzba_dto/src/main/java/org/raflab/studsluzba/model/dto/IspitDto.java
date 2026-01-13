package org.raflab.studsluzba.model.dto;

import java.time.LocalDate;
import java.time.LocalTime;
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
public class IspitDto {
    private Long id;
    private LocalDate datum;
    private Long predmetId;
    private Long nastavnikId;
    private LocalTime vremePocetka;
    private Boolean zakljucen;
    private Long ispitniRokId;
}
