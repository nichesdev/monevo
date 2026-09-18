package br.com.nichesdev.monevo_wallet.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

public record WalletBalanceResponseDto (UUID walletId, BigDecimal balance, String currency) {
}
