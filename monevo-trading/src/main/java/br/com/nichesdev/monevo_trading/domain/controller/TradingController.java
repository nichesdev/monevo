package br.com.nichesdev.monevo_trading.domain.controller;

import br.com.nichesdev.monevo_trading.domain.dto.TradeRequestDto;
import br.com.nichesdev.monevo_trading.domain.dto.TradeResponseDto;
import br.com.nichesdev.monevo_trading.domain.service.TradingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping("/monevo/trading")
@RequiredArgsConstructor
public class TradingController {

    private final TradingService tradingService;

    @PostMapping("/buy")
    public TradeResponseDto buy(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody TradeRequestDto request
    ) {
        return tradingService.buy(getUserId(jwt), request);
    }

    @PostMapping("/sell")
    public TradeResponseDto sell(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody TradeRequestDto request
    ) {
        return tradingService.sell(getUserId(jwt), request);
    }

    @GetMapping("/history")
    public Page<TradeResponseDto> history(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return tradingService.getHistory(getUserId(jwt), page, size);
    }

    @GetMapping("/{tradeId}")
    public TradeResponseDto getTrade(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID tradeId
    ) {
        return tradingService.getTrade(getUserId(jwt), tradeId);
    }

    private Long getUserId(Jwt jwt) {
        if (jwt != null) {
            try {
                long userId = Long.parseLong(
                        String.valueOf(jwt.getClaims().get("userId"))
                );
                if (userId > 0) {
                    return userId;
                }
            } catch (NumberFormatException ignored) {
            }
        }
        throw new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Token sem identificação válida do usuário"
        );
    }
}