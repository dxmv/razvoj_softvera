package org.raflab.studsluzba.model.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IspitPrikazDto {
    private Long id;
    private LocalDate datum;
    private LocalTime vremePocetka;
    private String nazivPredmeta;
    private Long predmetId;
    private Long nastavnikId;
    private Boolean zakljucen;

    @Override
    public String toString() {
        return nazivPredmeta;
    }
}