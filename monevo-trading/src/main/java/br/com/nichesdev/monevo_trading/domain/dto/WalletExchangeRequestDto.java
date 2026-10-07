package br.com.nichesdev.monevo_trading.domain.dto;

import br.com.nichesdev.monevo_trading.domain.model.TradeType;

import java.math.BigDecimal;
import java.util.UUID;

public record WalletExchangeRequestDto (
        UUID operationId,
        Long userId,
        TradeType type,
        String coin,
        BigDecimal quantity,
        BigDecimal totalBRL
) {
}
