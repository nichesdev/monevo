package br.com.nichesdev.market_data.CheckQuote;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.yaml.snakeyaml.error.Mark;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@Service
public class MarketDataService {

    private final MarketRepository marketRepository;

    public MarketDataService(MarketRepository marketRepository) {
        this.marketRepository = marketRepository;
    }

    public MarketDto processarCotacao(String coin){
        RestTemplate restTemplate = new RestTemplate();
        String apiUrl = "https://economia.awesomeapi.com.br/json/last/" + coin;

        try {
            String jsonPuro = restTemplate.getForObject(apiUrl, String.class);

            ObjectMapper mapper = new ObjectMapper();
            Map<String, MarketDto> mapaMoedas = mapper.readValue(jsonPuro, new  TypeReference<Map<String, MarketDto>>() {});

        if (mapaMoedas != null && !mapaMoedas.isEmpty()) {
            MarketDto coinDto = mapaMoedas.values().iterator().next();

            MarketEntity coinEntity = new MarketEntity();
            coinEntity.setCode(coinDto.getCode());
            coinEntity.setCodein(coinDto.getCodein());
            coinEntity.setName(coinDto.getName());
            coinEntity.setHigh(coinDto.getHigh());
            coinEntity.setLow(coinDto.getLow());

            marketRepository.save(coinEntity);

            return coinDto;
        }
        } catch (Exception e) {
            System.err.println("Erro ao processar a cotação para " + coin + ": " + e.getMessage());
        }
        return null;
    }
}