package org.raflab.studsluzba.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpisGodineEnrollmentRequest {
    private Long skolskaGodinaId;
    private Integer godinaStudija;
    private LocalDate datumUpisa;
    private String napomena;
    private Set<Long> prenetiPredmetIds;
}
