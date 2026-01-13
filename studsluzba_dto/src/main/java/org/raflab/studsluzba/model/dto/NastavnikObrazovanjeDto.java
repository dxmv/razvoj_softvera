package org.raflab.studsluzba.model.dto;

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
public class NastavnikObrazovanjeDto {
    private Long id;
    private Long nastavnikId;
    private Long visokaSkolaId;
    private Long vrstaStudijaId;
    private Integer godinaZavrsetka;
}
