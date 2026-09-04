package br.com.nichesdev.monevo_wallet.domain.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WalletDto {

    private long walletId;

    private String accountNumber;

    private BigDecimal balance;

}
