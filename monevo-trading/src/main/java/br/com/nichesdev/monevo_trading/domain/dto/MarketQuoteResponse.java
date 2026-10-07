package br.com.nichesdev.monevo_trading.domain.dto;

import java.time.LocalDateTime;

public record MarketQuoteResponse (
        Integer Id,
        String coinCode,
        String code,
        String codein,
        String name,
        String bid,
        String ask,
        LocalDateTime coinConsultation
) {
}
