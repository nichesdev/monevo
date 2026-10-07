package br.com.nichesdev.monevo_wallet.domain.dto;

import br.com.nichesdev.monevo_wallet.domain.model.WalletExchangeType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record WalletExchangeResponseDto (
        UUID operationId,
        UUID walletId,
        WalletExchangeType type,
        String coin,
        BigDecimal quantity,
        BigDecimal totalBRL,
        BigDecimal balanceBRLAfter,
        BigDecimal assetQuantityAfter,
        Instant processedAt
){
}
