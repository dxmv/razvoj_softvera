package org.raflab.studsluzba.model.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.raflab.studsluzba.model.NazivZvanja;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NastavnikZvanjeDto {
    private Long id;
    private Long nastavnikId;
    private NazivZvanja nazivZvanja;
    private LocalDate datumIzbora;
    private String uzaNaucnaOblast;
    private LocalDate datumPrestanka;
}
