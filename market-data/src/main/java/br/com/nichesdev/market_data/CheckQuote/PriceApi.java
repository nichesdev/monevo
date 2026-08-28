package br.com.nichesdev.market_data.CheckQuote;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("consulta-cotacao")
public class PriceApi {

    @GetMapping("{coin}")
    public CoinDto consultaMoeda(@PathVariable("coin") String coin) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<CoinDto> resp = restTemplate.getForEntity("https://economia.awesomeapi.com.br/json/last/{USD-BRL}?token=sk_uZyHPvEexGUpnrJae7omPX7OyDbJhnPyvWylWxQWAFzNJlEPaZ6EUnM1Yeem3", CoinDto.class);
        return resp.getBody();
    }
}
