package org.raflab.studsluzba.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrijavaIspitaPrikazDto {
    private String indeksPrikaz;
    private String ime;
    private String prezime;
    private LocalDate datumPrijave;
}