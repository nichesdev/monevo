package br.com.nichesdev.monevo_wallet.domain.dto;


import br.com.nichesdev.monevo_wallet.domain.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserCreatedListener {

    private final WalletService walletService;

    @KafkaListener(
            topics = "usuario-criado-topico",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(UserCreatedEvent event) {
        walletService.createWallet(event.userId());
        log.info("Evento recebido: userId={}", event.userId());
    }
}
