package org.raflab.studsluzba.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PredmetCreateDto {
    private String sifra;
    private String naziv;
    private String opis;
    private Integer espbBodovi;
    private Integer semestar;
    private Integer brPredavanja;
    private Integer brVezbi;
    private Long studijskiProgramId;
}