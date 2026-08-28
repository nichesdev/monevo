package br.com.nichesdev.market_data.CheckQuote;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@RestController
@RequestMapping("consulta-cotacao")
public class PriceApi {

    @GetMapping("teste/{coin}")
    public CoinDto consultaMoeda(@PathVariable("coin") String coin) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://economia.awesomeapi.com.br/json/last/" + coin;

        String jsonPuro = restTemplate.getForObject(url, String.class);

        ObjectMapper mapper = new ObjectMapper();
        Map<String, CoinDto> mapaMoedas = mapper.readValue(jsonPuro, new TypeReference<Map<String, CoinDto>>() {});

        if (mapaMoedas != null && !mapaMoedas.isEmpty()) {
            return mapaMoedas.values().iterator().next();
        }
        return null;
    }
}
