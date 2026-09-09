package br.com.nichesdev.monevo_wallet.domain.repository;

import br.com.nichesdev.monevo_wallet.domain.model.WalletEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface WalletRespository extends JpaRepository<WalletEntity, UUID> {
    Optional<WalletEntity> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
}
