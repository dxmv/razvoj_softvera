package org.raflab.studsluzba.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatistikaDto {
    private String nazivPredmeta;
    private Double prosek;
    private String period;
}