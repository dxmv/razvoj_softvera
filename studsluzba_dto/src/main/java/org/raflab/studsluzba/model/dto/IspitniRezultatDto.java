package org.raflab.studsluzba.model.dto;

import lombok.*;

@Getter@Setter
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IspitniRezultatDto {
    private String student;
    private String indeks;
    private String studProgram;
    private Double poeniIspit;
    private Double poeniPredispitni;
    private Double ukupno;
    private Integer ocena;
}