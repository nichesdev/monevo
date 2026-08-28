package br.com.nichesdev.market_data.CheckQuote;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MarketRepository extends JpaRepository<MarketEntity, Integer> {
    Optional<MarketEntity> findFirstByCoinCodeOrderByCoinConsultationDesc(String coin);
}
