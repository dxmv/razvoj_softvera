package org.raflab.studsluzba.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RemainingTuitionDto {
    private BigDecimal preostaloEur;
    private BigDecimal preostaloRsd;
    private BigDecimal primenjeniKurs;
}
