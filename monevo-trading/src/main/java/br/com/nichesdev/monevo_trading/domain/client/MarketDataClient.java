package br.com.nichesdev.monevo_trading.domain.client;

import org.springframework.stereotype.Component;
import br.com.nichesdev.monevo_trading.domain.dto.MarketQuoteResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Set;

@Component
public class MarketDataClient {

    private static final Set<String> SUPPORTED_COINS =
            Set.of("USD", "CAD", "EUR", "GBP", "BTC", "ETH");

    private final RestClient restClient;

    public MarketDataClient(
            @Qualifier("marketDataRestClient") RestClient restClient
    ) {
        this.restClient = restClient;
    }

    public MarketQuoteResponse getQuote(String coin) {
        if (coin == null || !SUPPORTED_COINS.contains(coin)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Moeda não suportada"
            );
        }
        MarketQuoteResponse quote;
        try {
            quote = restClient.get()
                    .uri("/consulta-cotacao/{pair}", coin + "-BRL")
                    .retrieve()
                    .body(MarketQuoteResponse.class);
        } catch (RestClientResponseException exception) {
            if (exception.getStatusCode().value() == 404) {
                throw new ResponseStatusException(
                        HttpStatus.SERVICE_UNAVAILABLE,
                        "Cotação indisponível para " + coin,
                        exception
                );
            }
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Market Data retornou erro ao consultar a cotação",
                    exception
            );

        } catch (RestClientException exception) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Não foi possível obter a cotação do Market Data",
                    exception
            );
        }
        validateQuote(coin, quote);
        return quote;
    }

    private void validateQuote(
            String requestedCoin,
            MarketQuoteResponse quote
    ) {
        if (quote == null) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Nenhuma cotação disponível para " + requestedCoin
            );
        }
        if (!requestedCoin.equals(quote.code())
                || !"BRL".equals(quote.codein())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Market Data retornou uma cotação de outro par"
            );
        }
        if (quote.coinConsultation() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Cotação sem data de consulta"
            );
        }
        try {
            BigDecimal bid = new BigDecimal(quote.bid());
            BigDecimal ask = new BigDecimal(quote.ask());

            if (bid.signum() <= 0 || ask.signum() <= 0) {
                throw new NumberFormatException("Preço não positivo");
            }
        } catch (NumberFormatException | NullPointerException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Market Data retornou preços inválidos",
                    exception
            );
        }
    }
}
