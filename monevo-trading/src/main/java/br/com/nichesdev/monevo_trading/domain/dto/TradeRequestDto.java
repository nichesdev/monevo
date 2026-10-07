package br.com.nichesdev.monevo_trading.domain.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record TradeRequestDto (
        @NotBlank(message = "Informe a moeda")
        @Pattern(
                regexp = "USD|CAD|EUR|GBP|BTC|ETH",
                message = "Moeda não suportada. Use USD, CAD, EUR, GBP, BTC ou ETH"
        )
        String coin,

        @NotNull(message = "Informe a quantidade")
        @DecimalMin(
                value = "0.00000001",
                message = "A quantidade mínima é 0.00000001"
        )
        @Digits(
                integer = 11,
                fraction = 8,
                message = "Use até 11 dígitos inteiros e 8 casas decimais"
        )
        BigDecimal quantity
){
}
