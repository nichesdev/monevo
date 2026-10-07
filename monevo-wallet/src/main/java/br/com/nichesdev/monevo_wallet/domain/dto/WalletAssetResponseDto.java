package br.com.nichesdev.monevo_wallet.domain.dto;

import java.math.BigDecimal;

public record WalletAssetResponseDto (String coin, BigDecimal quantity) {
}
