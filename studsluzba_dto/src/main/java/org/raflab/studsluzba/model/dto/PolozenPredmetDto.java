package org.raflab.studsluzba.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.raflab.studsluzba.model.NacinPolaganja;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PolozenPredmetDto {
    private Long id;
    private Long studentskiIndeksId;
    private Long predmetId;
    private Integer ocena;
    private NacinPolaganja nacinPolaganja;
    private Long izlazakNaIspitId;
    private Long visokoskolskaUstanovaId;
    private String nazivPredmetaSaDrugeVSU;
}
