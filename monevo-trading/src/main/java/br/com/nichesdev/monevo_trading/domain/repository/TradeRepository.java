package br.com.nichesdev.monevo_trading.domain.repository;

import br.com.nichesdev.monevo_trading.domain.model.TradeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface TradeRepository extends JpaRepository<TradeEntity, UUID> {

    Page<TradeEntity> findAllByOrderByCreatedAtDesc(Long userId, Pageable pageable);

    Optional<TradeEntity> findByTradeIdAndUserId(UUID tradeId, UUID userId);
}
