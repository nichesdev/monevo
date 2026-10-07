package br.com.nichesdev.monevo_trading.domain.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "trades")
@Getter
@Setter
@NoArgsConstructor
public class TradeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "trade_id")
    private UUID tradeId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TradeType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TradeStatus status;

    @Column(nullable = false, length = 10)
    private String coin;

    @Column(nullable = false, precision = 19, scale = 8)
    private BigDecimal quantity;

    @Column(name = "unite_price_brl", nullable = false, precision = 24, scale = 8)
    private BigDecimal unitePriceBrl;

    @Column(name = "total_brl", nullable = false, precision = 13, scale = 2)
    private BigDecimal totalBrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "failure_reason", length = 500)
    private String failureReason;

    @PrePersist
    public void onCreate()
    {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void onUpdate()
    {
        updatedAt = Instant.now();
    }
}
