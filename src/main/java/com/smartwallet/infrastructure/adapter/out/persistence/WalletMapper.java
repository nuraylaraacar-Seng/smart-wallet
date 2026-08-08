package com.smartwallet.infrastructure.adapter.out.persistence;

import com.smartwallet.domain.model.Money;
import com.smartwallet.domain.model.Wallet;
import com.smartwallet.domain.model.WalletStatus;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Component
public class WalletMapper {

    public Wallet toDomain(WalletEntity entity) {
        return Optional.ofNullable(entity)
                .map(e -> new Wallet(
                        e.getId(),
                        e.getUserId(),
                        Money.of(e.getBalanceAmount(), e.getBalanceCurrency()),
                        WalletStatus.valueOf(e.getStatus()),
                        e.getVersion()
                ))
                .orElse(null);
    }


    public WalletEntity toEntity(Wallet wallet, Instant existingCreatedAt) {
        return Optional.ofNullable(wallet)
                .map(w -> new WalletEntity(
                        w.getId(),
                        w.getUserId(),
                        w.getBalance().getAmount(),
                        w.getBalance().getCurrency().getCurrencyCode(),
                        w.getStatus().name(),
                        w.getVersion(),
                        existingCreatedAt != null ? existingCreatedAt : Instant.now(), // Kayıtlı tarih varsa onu kullan, yoksa şu anı al
                        Instant.now() // Güncellenme tarihi her zaman şu an olur
                ))
                .orElse(null);
    }

    public List<Wallet> toDomainList(List<WalletEntity> entities) {
        return Optional.ofNullable(entities)
                .orElseGet(List::of)
                .stream()
                .map(this::toDomain)
                .toList();
    }
}