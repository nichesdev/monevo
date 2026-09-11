package br.com.nichesdev.monevo_wallet.domain.controller;

import br.com.nichesdev.monevo_wallet.domain.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/monevo/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;


    @GetMapping("/balance")
    public ResponseEntity<BalanceResponseDto> getBalance() {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        BigDecimal balance = walletService.getBalance(currentUsername);
        return ResponseEntity.ok(new BalanceResponseDto(balance));
    }
}
