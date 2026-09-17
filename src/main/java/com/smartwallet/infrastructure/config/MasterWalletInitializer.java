package com.smartwallet.infrastructure.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class MasterWalletInitializer implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    public MasterWalletInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        UUID masterWalletId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        UUID systemUserId = UUID.fromString("00000000-0000-0000-0000-000000000000");

        // 1. Önce users tablosunda sistem kullanıcısı var mı kontrol et, yoksa ekle (NOT NULL kısıtını aşmak için)
        String checkUserSql = "SELECT COUNT(*) FROM users WHERE id = ?";
        Integer userCount = jdbcTemplate.queryForObject(checkUserSql, Integer.class, systemUserId);

        if (userCount != null && userCount == 0) {
            String insertUserSql = "INSERT INTO users (id, email, password_hash, status) VALUES (?, ?, ?, ?)";
            jdbcTemplate.update(insertUserSql, systemUserId, "system-master@smartwallet.internal", "SYSTEM_NO_LOGIN", "ACTIVE");
        }

        // 2. Master cüzdan var mı kontrol et, yoksa ekle
        String checkWalletSql = "SELECT COUNT(*) FROM wallets WHERE id = ?";
        Integer walletCount = jdbcTemplate.queryForObject(checkWalletSql, Integer.class, masterWalletId);

        if (walletCount != null && walletCount == 0) {
            String insertWalletSql = """
                        INSERT INTO wallets (id, user_id, balance_amount, balance_currency, status, version) 
                        VALUES (?, ?, ?, ?, ?, ?)
                    """;

            jdbcTemplate.update(insertWalletSql, masterWalletId, systemUserId, new BigDecimal("10000000.0000"), "TRY", "ACTIVE", 0L);
            System.out.println(">>> SİSTEM MASTER CÜZDANI BAŞARIYLA OLUŞTURULDU (10M TRY) <<<");
        }
    }
}