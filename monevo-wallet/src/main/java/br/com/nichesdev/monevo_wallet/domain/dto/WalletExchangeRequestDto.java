package br.com.nichesdev.monevo_wallet.domain.dto;

import br.com.nichesdev.monevo_wallet.domain.model.WalletExchangeType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.UUID;

public record WalletExchangeRequestDto (

        @NotNull
        UUID operationId,

        @NotNull
        @Positive
        Long userId,

        @NotNull
        WalletExchangeType type,

        @NotBlank
        @Pattern(
                regexp = "USD|CAD|EUR|GBP|BTC|ETH",
                message = "Moeda não suportada"
        )
        String coin,

        @NotNull
        @DecimalMin("0.00000001")
        @Digits(integer = 11, fraction = 8)
        BigDecimal quantity,

        @NotNull
        @DecimalMin("0.01")
        @Digits(integer = 11, fraction = 2)
        BigDecimal totalBRL
){
}
