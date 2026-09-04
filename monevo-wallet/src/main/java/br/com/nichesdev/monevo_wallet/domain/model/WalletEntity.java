package br.com.nichesdev.monevo_wallet.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.validator.constraints.UUID;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "wallet")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class WalletEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long walletId;

    private String accountNumber;

    private BigDecimal balance;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
