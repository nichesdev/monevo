package br.com.nichesdev.monevo_wallet.domain.repository;

import br.com.nichesdev.monevo_wallet.domain.model.WalletDeposityEntity;
import br.com.nichesdev.monevo_wallet.domain.model.WalletEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WalletDepositRepository extends JpaRepository<WalletDeposityEntity, UUID> {
}
