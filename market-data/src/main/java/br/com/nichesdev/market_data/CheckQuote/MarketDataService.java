package br.com.nichesdev.market_data.CheckQuote;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
public class MarketDataService {
    @Value("${market.coins:USD-BRL,CAD-BRL,EUR-BRL,GBP-BRL,BTC-BRL,ETH-BRL}")
    private String moedasConfiguradas;

    private final MarketRepository marketRepository;
    private final StringRedisTemplate redisTemplate;

    public MarketDataService(MarketRepository marketRepository, StringRedisTemplate redisTemplate) {
        this.marketRepository = marketRepository;
        this.redisTemplate = redisTemplate;
    }

    @Scheduled(fixedRate = 60000)
    public void processarCotacao() {
        RestTemplate restTemplate = new RestTemplate();
        String apiUrl = "https://economia.awesomeapi.com.br/json/last/" + moedasConfiguradas;

        try {
            String jsonPuro = restTemplate.getForObject(apiUrl, String.class);
            ObjectMapper mapper = new ObjectMapper();

            Map<String, MarketDto> mapaMoedas = mapper.readValue(jsonPuro, new TypeReference<Map<String, MarketDto>>() {
            });

            if (mapaMoedas != null && !mapaMoedas.isEmpty()) {

                for (Map.Entry<String, MarketDto> entry : mapaMoedas.entrySet()) {
                    String coinCodeExtraido = entry.getKey();
                    MarketDto marketDto = entry.getValue();

                    marketDto.setCoinCode(coinCodeExtraido);
                    marketDto.setCoinConsultation(LocalDateTime.now());

                    MarketEntity coinEntity = new MarketEntity();
                    coinEntity.setCoinCode(coinCodeExtraido);
                    coinEntity.setCode(marketDto.getCode());
                    coinEntity.setCodein(marketDto.getCodein());
                    coinEntity.setName(marketDto.getName());
                    coinEntity.setBid(marketDto.getBid());
                    coinEntity.setAsk(marketDto.getAsk());
                    coinEntity.setCoinConsultation(LocalDateTime.now());

                    marketRepository.save(coinEntity);
                try {
                    String dtoEmJson = mapper.writeValueAsString(marketDto);
                    String redisKey = "cotacao:" + coinCodeExtraido;
                    redisTemplate.opsForValue().set(redisKey, dtoEmJson, Duration.ofMinutes(15));
                }catch(Exception e) {
                System.err.println("falha ao salvar no Redis " + coinCodeExtraido + ": " + e.getMessage());}
                }
                System.out.println("Cotações sincronizadas no Postgres e Redis com sucesso.");
            }
        } catch (Exception e) {
            System.err.println("Erro ao sincronizar cotações: " + e.getMessage());
        }
    }

    public MarketDto buscarCotacaoNoCache(String coin) {
        String coinFormatada = coin.replace("-", "").toUpperCase();
        String redisKey = "cotacao:" + coinFormatada;
        try {
            String resultadoRedis = redisTemplate.opsForValue().get(redisKey);
            if (resultadoRedis != null) {
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(resultadoRedis, MarketDto.class);
            }
        } catch (Exception e) {
            System.err.println("Redis indisponível ou falhou na leitura: " + e.getMessage());
        }

        try {
            Optional<MarketEntity> optionalEntity = marketRepository
                    .findFirstByCoinCodeOrderByCoinConsultationDesc(coinFormatada);

            if (optionalEntity.isPresent()) {
                MarketEntity entity = optionalEntity.get();
                return MarketDto.builder()
                        .coinCode(entity.getCoinCode())
                        .code(entity.getCode())
                        .codein(entity.getCodein())
                        .name(entity.getName())
                        .bid(entity.getBid())
                        .ask(entity.getAsk())
                        .coinConsultation(entity.getCoinConsultation())
                        .build();
            }
        } catch (Exception e) {
            System.err.println("Erro ao consultar banco no fallback: " + e.getMessage());
        }
        return null;
    }
}
