package org.raflab.studsluzba.model.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.raflab.studsluzba.model.StatusIndeksa;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IndeksDto {
    private Long id;
    private Long studentId;
    private Integer godinaUpisa;
    private Integer brojIndeksa;
    private Long studijskiProgramId;
    private StatusIndeksa status;
    private LocalDate datumAktivacije;
    private LocalDate datumDeaktivacije;
}
