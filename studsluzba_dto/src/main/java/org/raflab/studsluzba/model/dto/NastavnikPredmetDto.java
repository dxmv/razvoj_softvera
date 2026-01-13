package org.raflab.studsluzba.model.dto;

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
public class NastavnikPredmetDto {
    private Long id;
    private Long skolskaGodinaId;
    private Long nastavnikId;
    private Long predmetId;
}
