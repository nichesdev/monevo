package br.com.nichesdev.monevo_wallet.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WalletBalanceResponseDto {
    private BigDecimal balance;
}
