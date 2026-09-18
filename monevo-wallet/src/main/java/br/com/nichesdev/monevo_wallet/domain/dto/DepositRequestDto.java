package br.com.nichesdev.monevo_wallet.domain.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record DepositRequestDto (
        @NotNull(message = "Informe o Valor")
        @DecimalMin(value = "0.01", message = "O valor minimo é 0.01")
        @Digits(integer = 11, fraction = 2, message = "Use até 11 digitos inteiros e 2 casas deciamais")
        BigDecimal amount){
}
