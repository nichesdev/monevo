package br.com.nichesdev.market_data.CheckQuote;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "market")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class MarketEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;
    private String coinCode;
    private String code;
    private String codein;
    private String name;
    private String bid;
    private String ask;
    private LocalDateTime coinConsultation;
}
