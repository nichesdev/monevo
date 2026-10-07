package br.com.nichesdev.monevo_trading.domain.dto;

import br.com.nichesdev.monevo_trading.domain.model.TradeStatus;
import br.com.nichesdev.monevo_trading.domain.model.TradeType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TradeResponseDto (
        UUID tradeId,
        TradeType type,
        TradeStatus status,
        String coin,
        BigDecimal quantity,
        BigDecimal unitPriceBRL,
        BigDecimal totalBRL,
        Instant createdAt
){
}
