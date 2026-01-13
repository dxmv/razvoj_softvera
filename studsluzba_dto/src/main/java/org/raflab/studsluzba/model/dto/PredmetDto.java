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
public class PredmetDto {
    private Long id;
    private String sifra;
    private String naziv;
    private String opis;
    private Integer espbBodovi;
    private Integer semestar;
    private Integer brPredavanja;
    private Integer brVezbi;
    private Long studijskiProgramId;
}
