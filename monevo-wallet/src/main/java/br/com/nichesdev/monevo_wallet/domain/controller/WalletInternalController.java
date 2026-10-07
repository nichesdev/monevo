package br.com.nichesdev.monevo_wallet.domain.controller;

import br.com.nichesdev.monevo_wallet.domain.dto.WalletExchangeRequestDto;
import br.com.nichesdev.monevo_wallet.domain.dto.WalletExchangeResponseDto;
import br.com.nichesdev.monevo_wallet.domain.service.WalletExchangeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/wallet")
@RequiredArgsConstructor
public class WalletInternalController {

    private final WalletExchangeService exchangeService;


    @PostMapping("/exchanges")
    public WalletExchangeResponseDto exchange(@Valid @RequestBody WalletExchangeRequestDto request) {
        return exchangeService.exchange(request);
    }
}
