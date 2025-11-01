package org.raflab.studsluzba.model.dto;

import java.time.LocalDateTime;
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
public class PrijavaIspitaDto {
    private Long id;
    private Long studentskiIndeksId;
    private Long ispitId;
    private LocalDateTime datumPrijave;
}
