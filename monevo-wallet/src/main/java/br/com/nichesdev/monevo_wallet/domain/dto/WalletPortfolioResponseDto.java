package br.com.nichesdev.monevo_wallet.domain.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record WalletPortfolioResponseDto (UUID walletId, BigDecimal balanceBRL, List<WalletAssetResponseDto> assets) {
}
