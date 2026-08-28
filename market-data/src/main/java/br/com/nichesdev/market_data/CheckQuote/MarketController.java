package br.com.nichesdev.market_data.CheckQuote;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("consulta-cotacao")
public class MarketController {

    private final MarketDataService marketDataService;

    public MarketController(MarketDataService marketDataService) {
        this.marketDataService = marketDataService;
    }

    @GetMapping("/{coin}")
    public MarketDto consultaMoeda(@PathVariable("coin") String coin){
        return marketDataService.buscarCotacaoNoCache(coin);
    }

}