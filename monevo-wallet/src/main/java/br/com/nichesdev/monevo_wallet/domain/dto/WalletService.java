package br.com.nichesdev.monevo_wallet.domain.dto;


import br.com.nichesdev.monevo_wallet.domain.model.WalletEntity;
import br.com.nichesdev.monevo_wallet.domain.repository.WalletRespository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRespository walletRespository;

    @Transactional
    public void createWallet(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId é obrigatório");
        }

        if(walletRespository.existsByUserId(userId)) {
            return;
        }

        WalletEntity wallet = new WalletEntity();
        wallet.setUserId(userId);
        wallet.setBalance(BigDecimal.ZERO);

        walletRespository.save(wallet);
    }
}
