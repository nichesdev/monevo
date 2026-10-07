package br.com.nichesdev.monevo_wallet.domain.repository;

import br.com.nichesdev.monevo_wallet.domain.model.WalletAssetEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WalletAssetRepository extends JpaRepository<WalletAssetEntity, UUID> {

    Optional<WalletAssetEntity> findByWallet_WalletIdAndCoin(UUID walletId, String coin);

    List<WalletAssetEntity> findAllByWallet_WalletIdOrderByCoinAsc(UUID walletId);
}
