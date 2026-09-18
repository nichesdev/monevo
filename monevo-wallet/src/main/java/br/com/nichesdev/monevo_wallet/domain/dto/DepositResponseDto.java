package br.com.nichesdev.monevo_wallet.domain.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record DepositResponseDto (UUID depositId, UUID walletId, BigDecimal amount, BigDecimal balancerAfter, String currency, Instant createdAt) {
}
