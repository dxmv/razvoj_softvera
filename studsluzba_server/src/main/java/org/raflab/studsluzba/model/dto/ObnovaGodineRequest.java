package org.raflab.studsluzba.model.dto;

import java.time.LocalDate;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ObnovaGodineRequest {
    private Integer godinaStudija;
    private LocalDate datumObnove;
    private String napomena;
    private Set<Long> predmetIds;
}
