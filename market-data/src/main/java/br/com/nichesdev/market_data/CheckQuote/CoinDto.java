package br.com.nichesdev.market_data.CheckQuote;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CoinDto {

    private String code;
    private String codein;
    private String name;
    private String high;
    private String low;

}
