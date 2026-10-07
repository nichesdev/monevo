package br.com.nichesdev.monevo_trading.domain.dto;

import br.com.nichesdev.monevo_trading.domain.model.TradeType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record WalletExchangeResponseDto (
        UUID operationId,
        UUID walletId,
        TradeType type,
        String coin,
        BigDecimal quantity,
        BigDecimal totalBRL,
        BigDecimal balanceBRLAfter,
        BigDecimal assetQuantityAfter,
        Instant processedAt
) {
}
