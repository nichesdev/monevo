package br.com.nichesdev.monevo_wallet.domain.service;


import br.com.nichesdev.monevo_wallet.domain.dto.DepositRequestDto;
import br.com.nichesdev.monevo_wallet.domain.dto.DepositResponseDto;
import br.com.nichesdev.monevo_wallet.domain.dto.WalletBalanceResponseDto;
import br.com.nichesdev.monevo_wallet.domain.model.WalletDeposityEntity;
import br.com.nichesdev.monevo_wallet.domain.model.WalletEntity;
import br.com.nichesdev.monevo_wallet.domain.repository.WalletDepositRepository;
import br.com.nichesdev.monevo_wallet.domain.repository.WalletRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class WalletService {

    private static final BigDecimal MAX_BALANCE = new BigDecimal("99999999999.99999999");
    private static final String CURRENCY = "BRL";
    private final WalletRepository walletRepository;
    private final WalletDepositRepository depositRepository;
    private final EntityManager entityManager;

    @Transactional
    public void createWallet(Long userId) {
        validateUserId(userId);

        if (walletRepository.existsByUserId(userId)) {
            return;
        }
        WalletEntity wallet = new WalletEntity();
        wallet.setUserId(userId);
        wallet.setBalance(BigDecimal.ZERO);

        walletRepository.save(wallet);
    }

    @Transactional(readOnly = true)
    public WalletBalanceResponseDto getBalance(Long userId){
        validateUserId(userId);

        WalletEntity wallet = findWallet(userId);

        return new WalletBalanceResponseDto(
                wallet.getWalletId(),
                wallet.getBalance(),
                CURRENCY
        );
    }

    public DepositResponseDto deposit(Long userId, DepositRequestDto request){
        validateUserId(userId);
        BigDecimal amount = validateAmount(request);
        WalletEntity wallet = findWallet(userId);

        entityManager.refresh(wallet, LockModeType.PESSIMISTIC_WRITE);

        BigDecimal balanceAfter = wallet.getBalance().add(amount);

        if(balanceAfter.compareTo(MAX_BALANCE) > 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O Depósito ultrapassa o limite de saldo da carteira");
        }

        wallet.setBalance(balanceAfter);
        walletRepository.save(wallet);

        WalletDeposityEntity deposit = new WalletDeposityEntity();
        deposit.setWallet(wallet);
        deposit.setAmount(amount);
        deposit.setBalanceAfter(balanceAfter);
        deposit.setCreatedAt(Instant.now());

        WalletDeposityEntity savedDeposit = depositRepository.save(deposit);

        return new DepositResponseDto(
                savedDeposit.getDeposityId(),
                wallet.getWalletId(),
                savedDeposit.getAmount(),
                savedDeposit.getBalanceAfter(),
                CURRENCY,
                savedDeposit.getCreatedAt()
        );
    }

    private WalletEntity findWallet(Long userId) {
        return walletRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Carteira não encontrada. O cadastro pode estar em processamento."
                ));
    }

    private void validateUserId(Long userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException(
                    "userId deve ser informado e positivo"
            );
        }
    }

    private BigDecimal validateAmount(DepositRequestDto request) {
        if (request == null || request.amount() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Informe o valor do depósito"
            );
        }

        BigDecimal amount = request.amount();

        if (amount.compareTo(new BigDecimal("0.01")) < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O valor mínimo do depósito é 0.01"
            );
        }

        if (amount.compareTo(MAX_BALANCE) > 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Valor do depósito acima do limite"
            );
        }

        try {
            return amount.setScale(2, RoundingMode.UNNECESSARY);
        } catch (ArithmeticException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Use no máximo duas casas decimais"
            );
        }
    }
}
