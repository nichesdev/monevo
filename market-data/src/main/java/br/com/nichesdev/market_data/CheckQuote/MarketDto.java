package br.com.nichesdev.market_data.CheckQuote;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MarketDto {

    private Integer id;
    private String coinCode;
    private String code;
    private String codein;
    private String name;
    private String bid;
    private String ask;
    private LocalDateTime coinConsultation;
}
