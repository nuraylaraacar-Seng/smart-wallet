package com.smartwallet.infrastructure.adapter.out.messaging;

import com.smartwallet.domain.event.TransactionCompletedEvent;
import com.smartwallet.infrastructure.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class TransactionEventListener {

    private final RabbitTemplate rabbitTemplate;

    public TransactionEventListener(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleTransactionCompleted(TransactionCompletedEvent event) {
        // Dual-Write problemini önlemek için SADECE veritabanı commit'i başarılı olduktan sonra RabbitMQ'ya gönderiyorum
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.ROUTING_KEY, event);
    }
}