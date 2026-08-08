package com.smartwallet.infrastructure.adapter.out.persistence;

import com.smartwallet.application.port.out.TransactionRepositoryPort;
import com.smartwallet.domain.model.Money;
import com.smartwallet.domain.model.Transaction;
import com.smartwallet.domain.model.TransactionStatus;
import com.smartwallet.domain.model.TransactionType;
import org.springframework.stereotype.Component;

import java.util.Currency;
import java.util.Optional;
import java.util.UUID;

@Component
public class TransactionRepositoryAdapter implements TransactionRepositoryPort {

    private final SpringDataTransactionRepository repository;

    public TransactionRepositoryAdapter(SpringDataTransactionRepository repository) {
        this.repository = repository;
    }

    @Override
    public Transaction save(Transaction transaction) {
        TransactionEntity entity = new TransactionEntity();

        entity.setId(transaction.getId());
        entity.setSourceWalletId(transaction.getSourceWalletId());
        entity.setTargetWalletId(transaction.getTargetWalletId());


        if (transaction.getAmount() != null) {
            entity.setAmountValue(transaction.getAmount().getAmount());
            entity.setAmountCurrency(transaction.getAmount().getCurrency().getCurrencyCode());
        }

        entity.setType(transaction.getType().name());
        entity.setStatus(transaction.getStatus().name());

        entity.setCreatedAt(transaction.getCreatedAt());
        entity.setIdempotencyKey(transaction.getIdempotencyKey());

        TransactionEntity savedEntity = repository.save(entity);

        return mapToDomain(savedEntity);
    }


    @Override
    public Optional<Transaction> findById(UUID id) {
        return repository.findById(id).map(this::mapToDomain);
    }

    @Override
    public Optional<Transaction> findByIdempotencyKey(String idempotencyKey) {
        return repository.findByIdempotencyKey(idempotencyKey).map(this::mapToDomain);
    }

    private Transaction mapToDomain(TransactionEntity entity) {
        Money amount = Money.of(entity.getAmountValue(), Currency.getInstance(entity.getAmountCurrency()));

        return new Transaction(
                entity.getId(),
                entity.getSourceWalletId(),
                entity.getTargetWalletId(),
                amount,
                TransactionType.valueOf(entity.getType()),
                TransactionStatus.valueOf(entity.getStatus()),
                entity.getCreatedAt(),
                entity.getIdempotencyKey()
        );
    }
}