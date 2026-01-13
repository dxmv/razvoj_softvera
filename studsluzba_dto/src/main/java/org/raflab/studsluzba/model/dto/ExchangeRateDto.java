package org.raflab.studsluzba.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ExchangeRateDto {

    private String code;
    private LocalDate date;

    @JsonProperty("date_from")
    private LocalDate dateFrom;

    private Integer number;
    private BigDecimal parity;

    @JsonProperty("cash_buy")
    private BigDecimal cashBuy;

    @JsonProperty("cash_sell")
    private BigDecimal cashSell;

    @JsonProperty("exchange_buy")
    private BigDecimal exchangeBuy;

    @JsonProperty("exchange_middle")
    private BigDecimal exchangeMiddle;

    @JsonProperty("exchange_sell")
    private BigDecimal exchangeSell;
}

