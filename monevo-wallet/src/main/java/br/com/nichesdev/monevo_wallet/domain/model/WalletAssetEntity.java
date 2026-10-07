package br.com.nichesdev.monevo_wallet.domain.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name= "wallet_asset")
@Getter
@Setter
@NoArgsConstructor
public class WalletAssetEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID assetId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "wallet_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_asset_wallet")
    )
    private WalletEntity wallet;

    @Column(nullable = false, length = 10)
    private String coin;

    @Column(nullable = false, precision = 19, scale = 8)
    private BigDecimal quantity;
}
