package org.raflab.studsluzba.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NastavnikDto {
    private Long id;
    private String ime;
    private String prezime;
    private String srednjeIme;
    private String email;
    private Set<NastavnikObrazovanjeDto> obrazovanje;
}
