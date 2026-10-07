package br.com.nichesdev.monevo_trading.domain.client;


import br.com.nichesdev.monevo_trading.domain.dto.WalletExchangeRequestDto;
import br.com.nichesdev.monevo_trading.domain.dto.WalletExchangeResponseDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

@Component
public class WalletClient {

    private final RestClient restClient;

    public WalletClient(
            @Qualifier("walletRestClient") RestClient restClient
    ) {
        this.restClient = restClient;
    }
    public WalletExchangeResponseDto exchange(
            WalletExchangeRequestDto request
    ) {
        WalletExchangeResponseDto response;
        try {
            response = restClient.post()
                    .uri("/internal/wallet/exchanges")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(WalletExchangeResponseDto.class);
        } catch (RestClientResponseException exception) {
            int status = exception.getStatusCode().value();
            if (status == 409) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Saldo em BRL ou quantidade de moeda insuficiente",
                        exception
                );
            }
            if (status == 404) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Carteira não encontrada",
                        exception
                );
            }
            if (status == 400) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Wallet recusou os valores da operação",
                        exception
                );
            }
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Falha na integração com a Wallet; confirme o resultado da operação",
                    exception
            );
        } catch (RestClientException exception) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Não foi possível confirmar o resultado da operação na Wallet",
                    exception
            );
        }
        if (response == null
                || !request.operationId().equals(response.operationId())
                || response.walletId() == null
                || request.type() != response.type()
                || !request.coin().equals(response.coin())
                || response.quantity() == null
                || request.quantity().compareTo(response.quantity()) != 0
                || response.totalBRL() == null
                || request.totalBRL().compareTo(response.totalBRL()) != 0
                || response.balanceBRLAfter() == null
                || response.assetQuantityAfter() == null
                || response.processedAt() == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Wallet retornou uma confirmação inválida"
            );
        }
        return response;
    }
}
