package org.raflab.studsluzba.model.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ExchangeRateDto {

    private String code;
    private LocalDate date;

    private LocalDate dateFrom;

    private Integer number;
    private BigDecimal parity;

    private BigDecimal cashBuy;

    private BigDecimal cashSell;

    private BigDecimal exchangeBuy;

    private BigDecimal exchangeMiddle;

    private BigDecimal exchangeSell;
}

