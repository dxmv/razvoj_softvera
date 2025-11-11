package org.raflab.studsluzba.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UplataDto {
    private Long id;
    private Long upisGodineId;
    private LocalDate datumUplate;
    private BigDecimal iznosUDinarima;
    private BigDecimal srednjiKurs;
}
