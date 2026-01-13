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
public class ObnovaGodineDto {
    private Long id;
    private Long studentskiIndeksId;
    private Long skolskaGodinaId;
    private Integer godinaStudija;
    private LocalDate datumObnove;
    private String napomena;
    private Set<Long> predmetiIds;
}
