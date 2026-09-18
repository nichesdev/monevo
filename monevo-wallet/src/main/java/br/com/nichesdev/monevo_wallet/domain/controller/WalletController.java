package br.com.nichesdev.monevo_wallet.domain.controller;

import br.com.nichesdev.monevo_wallet.domain.dto.DepositRequestDto;
import br.com.nichesdev.monevo_wallet.domain.dto.DepositResponseDto;
import br.com.nichesdev.monevo_wallet.domain.dto.WalletBalanceResponseDto;
import br.com.nichesdev.monevo_wallet.domain.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/monevo/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @GetMapping("/balance")
    public WalletBalanceResponseDto getBalance(@AuthenticationPrincipal Jwt jwt) {
        Long userId = getUserId(jwt);
        return walletService.getBalance(userId);
    }

    @PostMapping("/deposits")
    public DepositResponseDto deposit(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody DepositRequestDto request) {
        Long userId = getUserId(jwt);

        return walletService.deposit(userId, request);
    }

    private Long getUserId(Jwt jwt) {
        Object userId = jwt.getClaims().get("userId");

        return Long.parseLong(String.valueOf(userId));
    }
}