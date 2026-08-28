package br.com.nichesdev.market_data.CheckQuote;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "market")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class CoinEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;
    private String code;
    private String codein;
    private String name;
    private String high;
    private String low;
}
