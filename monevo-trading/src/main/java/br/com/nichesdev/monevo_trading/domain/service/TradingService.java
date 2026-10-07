package br.com.nichesdev.monevo_trading.domain.service;

import br.com.nichesdev.monevo_trading.domain.client.MarketDataClient;
import br.com.nichesdev.monevo_trading.domain.client.WalletClient;
import br.com.nichesdev.monevo_trading.domain.dto.MarketQuoteResponse;
import br.com.nichesdev.monevo_trading.domain.dto.TradeRequestDto;
import br.com.nichesdev.monevo_trading.domain.dto.TradeResponseDto;
import br.com.nichesdev.monevo_trading.domain.dto.WalletExchangeRequestDto;
import br.com.nichesdev.monevo_trading.domain.model.TradeEntity;
import br.com.nichesdev.monevo_trading.domain.model.TradeStatus;
import br.com.nichesdev.monevo_trading.domain.model.TradeType;
import br.com.nichesdev.monevo_trading.domain.repository.TradeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.UUID;

@Service
public class TradingService {

    private static final BigDecimal MAX_TOTAL =
            new BigDecimal("99999999999.99");

    private static final Duration MAX_QUOTE_AGE =
            Duration.ofMinutes(5);

    private final TradeRepository tradeRepository;
    private final MarketDataClient marketDataClient;
    private final WalletClient walletClient;
    private final TransactionTemplate transaction;

    public TradingService(
            TradeRepository tradeRepository,
            MarketDataClient marketDataClient,
            WalletClient walletClient,
            PlatformTransactionManager transactionManager
    ) {
        this.tradeRepository = tradeRepository;
        this.marketDataClient = marketDataClient;
        this.walletClient = walletClient;

        this.transaction = new TransactionTemplate(transactionManager);
        this.transaction.setPropagationBehavior(
                TransactionDefinition.PROPAGATION_REQUIRES_NEW
        );
    }

    public TradeResponseDto buy(Long userId, TradeRequestDto request) {
        return execute(userId, request, TradeType.BUY);
    }

    public TradeResponseDto sell(Long userId, TradeRequestDto request) {
        return execute(userId, request, TradeType.SELL);
    }

    public Page<TradeResponseDto> getHistory(
            Long userId,
            int page,
            int size
    ) {
        validateUserId(userId);

        if (page < 0 || size < 1 || size > 100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Use page >= 0 e size entre 1 e 100"
            );
        }

        return tradeRepository
                .findAllByUserIdOrderByCreatedAtDesc(
                        userId,
                        PageRequest.of(page, size)
                )
                .map(this::toResponse);
    }

    public TradeResponseDto getTrade(Long userId, UUID tradeId) {
        validateUserId(userId);

        TradeEntity trade = tradeRepository
                .findByTradeIdAndUserId(tradeId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Negociação não encontrada"
                ));

        return toResponse(trade);
    }

    private TradeResponseDto execute(
            Long userId,
            TradeRequestDto request,
            TradeType type
    ) {
        validateUserId(userId);

        if (request == null || request.quantity() == null
                || request.quantity().signum() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Informe uma quantidade positiva"
            );
        }

        BigDecimal quantity;

        try {
            quantity = request.quantity()
                    .setScale(8, RoundingMode.UNNECESSARY);
        } catch (ArithmeticException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Use no máximo oito casas decimais na quantidade"
            );
        }

        if (quantity.precision() > 19) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Quantidade acima do limite"
            );
        }

        MarketQuoteResponse quote =
                marketDataClient.getQuote(request.coin());

        validateQuoteAge(quote);

        BigDecimal unitPrice = getUnitPrice(quote, type);

        BigDecimal total = quantity.multiply(unitPrice)
                .setScale(
                        2,
                        type == TradeType.BUY
                                ? RoundingMode.CEILING
                                : RoundingMode.FLOOR
                );

        if (total.compareTo(new BigDecimal("0.01")) < 0
                || total.compareTo(MAX_TOTAL) > 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O total deve estar entre R$ 0,01 e R$ 99.999.999.999,99"
            );
        }

        TradeEntity trade = new TradeEntity();
        trade.setUserId(userId);
        trade.setType(type);
        trade.setStatus(TradeStatus.PENDING);
        trade.setCoin(request.coin());
        trade.setQuantity(quantity);
        trade.setUnitePriceBrl(unitPrice);
        trade.setTotalBrl(total);

        // Confirma o registro no banco antes de chamar a Wallet.
        TradeEntity pending = Objects.requireNonNull(
                transaction.execute(status ->
                        tradeRepository.saveAndFlush(trade))
        );

        UUID tradeId = pending.getTradeId();

        WalletExchangeRequestDto walletRequest =
                new WalletExchangeRequestDto(
                        tradeId,
                        userId,
                        type,
                        request.coin(),
                        quantity,
                        total
                );

        try {
            walletClient.exchange(walletRequest);

        } catch (ResponseStatusException exception) {
            int status = exception.getStatusCode().value();

            boolean explicitRejection =
                    status == 400 || status == 404 || status == 409;

            updateStatus(
                    tradeId,
                    explicitRejection
                            ? TradeStatus.FAILED
                            : TradeStatus.UNKNOWN,
                    exception.getReason()
            );

            throw new ResponseStatusException(
                    exception.getStatusCode(),
                    exception.getReason() + ". Negociação: " + tradeId,
                    exception
            );
        }

        // Este bloco fica fora do catch da chamada HTTP:
        // a Wallet já confirmou a movimentação.
        TradeEntity completed;

        try {
            completed = updateStatus(
                    tradeId,
                    TradeStatus.COMPLETED,
                    null
            );
        } catch (RuntimeException exception) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Wallet confirmou a movimentação, mas o registro final "
                            + "falhou. Não repita a operação. Negociação: "
                            + tradeId,
                    exception
            );
        }

        return toResponse(completed);
    }

    private TradeEntity updateStatus(
            UUID tradeId,
            TradeStatus newStatus,
            String reason
    ) {
        return Objects.requireNonNull(
                transaction.execute(status -> {
                    TradeEntity trade = tradeRepository.findById(tradeId)
                            .orElseThrow(() ->
                                    new IllegalStateException(
                                            "Negociação não encontrada: "
                                                    + tradeId
                                    ));

                    trade.setStatus(newStatus);
                    trade.setFailureReason(
                            reason == null
                                    ? null
                                    : reason.substring(
                                    0,
                                    Math.min(reason.length(), 500)
                            )
                    );

                    return tradeRepository.saveAndFlush(trade);
                })
        );
    }

    private BigDecimal getUnitPrice(
            MarketQuoteResponse quote,
            TradeType type
    ) {
        try {
            String price = type == TradeType.BUY
                    ? quote.ask()
                    : quote.bid();

            BigDecimal unitPrice = new BigDecimal(price)
                    .setScale(8, RoundingMode.UNNECESSARY);

            if (unitPrice.signum() <= 0 || unitPrice.precision() > 24) {
                throw new NumberFormatException("Preço fora do limite");
            }

            return unitPrice;

        } catch (NumberFormatException | ArithmeticException
                 | NullPointerException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Cotação inválida para executar a negociação",
                    exception
            );
        }
    }

    private void validateQuoteAge(MarketQuoteResponse quote) {
        LocalDateTime collectedAt = quote.coinConsultation();
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        if (collectedAt == null
                || collectedAt.isBefore(now.minus(MAX_QUOTE_AGE))
                || collectedAt.isAfter(now.plusSeconds(30))) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Cotação desatualizada ou com horário inválido"
            );
        }
    }

    private void validateUserId(Long userId) {
        if (userId == null || userId <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuário não identificado"
            );
        }
    }

    private TradeResponseDto toResponse(TradeEntity trade) {
        return new TradeResponseDto(
                trade.getTradeId(),
                trade.getType(),
                trade.getStatus(),
                trade.getCoin(),
                trade.getQuantity(),
                trade.getUnitePriceBrl(),
                trade.getTotalBrl(),
                trade.getCreatedAt()
        );
    }
}