package org.raflab.studsluzba.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@AllArgsConstructor
public class RezultatIspitaStudentDto {
        private String brojIndeksa;
        private String studProgramOznaka;
        private Integer godinaUpisa;
        private Integer broj;
        private String ime;
        private String prezime;
        private Double ukupnoPoena;
}
