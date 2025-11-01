package org.raflab.studsluzba.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.raflab.studsluzba.model.VrstaPredispitneObaveze;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PredispitnaObavezaDto {
    private Long id;
    private Long nastavnikPredmetId;
    private VrstaPredispitneObaveze vrsta;
    private Double maksimalanBrojPoena;
}
