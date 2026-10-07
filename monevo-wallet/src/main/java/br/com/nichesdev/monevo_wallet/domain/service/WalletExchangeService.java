package br.com.nichesdev.monevo_wallet.domain.service;

import br.com.nichesdev.monevo_wallet.domain.dto.WalletExchangeRequestDto;
import br.com.nichesdev.monevo_wallet.domain.dto.WalletExchangeResponseDto;
import br.com.nichesdev.monevo_wallet.domain.model.WalletAssetEntity;
import br.com.nichesdev.monevo_wallet.domain.model.WalletEntity;
import br.com.nichesdev.monevo_wallet.domain.model.WalletExchangeType;
import br.com.nichesdev.monevo_wallet.domain.repository.WalletAssetRepository;
import br.com.nichesdev.monevo_wallet.domain.repository.WalletRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class WalletExchangeService {

    private static final BigDecimal MAX_VALUE =
            new BigDecimal("99999999999.99999999");

    private final WalletRepository walletRepository;
    private final WalletAssetRepository assetRepository;
    private final EntityManager entityManager;

    @Transactional
    public WalletExchangeResponseDto exchange(
            WalletExchangeRequestDto request
    ) {
        WalletEntity wallet = walletRepository
                .findByUserId(request.userId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Carteira não encontrada"
                ));

        entityManager.refresh(wallet, LockModeType.PESSIMISTIC_WRITE);

        BigDecimal quantity = request.quantity()
                .setScale(8, RoundingMode.UNNECESSARY);

        BigDecimal totalBRL = request.totalBRL()
                .setScale(2, RoundingMode.UNNECESSARY);

        WalletAssetEntity asset = assetRepository
                .findByWallet_WalletIdAndCoin(
                        wallet.getWalletId(),
                        request.coin()
                )
                .orElse(null);

        BigDecimal currentQuantity = asset == null
                ? BigDecimal.ZERO
                : asset.getQuantity();

        BigDecimal balanceAfter;
        BigDecimal quantityAfter;

        if (request.type() == WalletExchangeType.BUY) {
            if (wallet.getBalance().compareTo(totalBRL) < 0) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Saldo em BRL insuficiente"
                );
            }

            balanceAfter = wallet.getBalance().subtract(totalBRL);
            quantityAfter = currentQuantity.add(quantity);
        } else {
            if (currentQuantity.compareTo(quantity) < 0) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Quantidade de moeda insuficiente"
                );
            }

            balanceAfter = wallet.getBalance().add(totalBRL);
            quantityAfter = currentQuantity.subtract(quantity);
        }

        if (balanceAfter.compareTo(MAX_VALUE) > 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Operação ultrapassa o limite de saldo em BRL"
            );
        }

        if (quantityAfter.compareTo(MAX_VALUE) > 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Operação ultrapassa o limite de quantidade da moeda"
            );
        }

        if (asset == null) {
            asset = new WalletAssetEntity();
            asset.setWallet(wallet);
            asset.setCoin(request.coin());
        }

        wallet.setBalance(balanceAfter);
        asset.setQuantity(quantityAfter);

        walletRepository.save(wallet);
        assetRepository.save(asset);

        return new WalletExchangeResponseDto(
                request.operationId(),
                wallet.getWalletId(),
                request.type(),
                request.coin(),
                quantity,
                totalBRL,
                balanceAfter,
                quantityAfter,
                Instant.now()
        );
    }
}
