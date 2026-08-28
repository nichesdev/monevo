package br.com.nichesdev.market_data.CheckQuote;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MarketDto {

    private String code;
    private String codein;
    private String name;
    private String high;
    private String low;

}
