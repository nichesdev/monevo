package br.com.nichesdev.monevo_wallet.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.validator.constraints.UUID;
import java.math.BigDecimal;

@Entity
@Table(name = "wallet")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class WalletEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID walletId;

    @Column(nullable = false, unique = true)
    private Long userId;

    @Column(nullable = false, precision = 19, scale = 8)
    private BigDecimal balance;
}
