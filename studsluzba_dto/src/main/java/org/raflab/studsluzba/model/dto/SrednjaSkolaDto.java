package org.raflab.studsluzba.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.raflab.studsluzba.model.VrstaSkole;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SrednjaSkolaDto {
    private Long id;
    private String naziv;
    private String mesto;
    private VrstaSkole vrsta;
}
