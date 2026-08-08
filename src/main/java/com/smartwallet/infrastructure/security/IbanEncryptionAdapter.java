package com.smartwallet.infrastructure.security;

import com.smartwallet.application.port.out.IbanEncryptionPort;
import com.smartwallet.domain.model.EncryptedIban;
import org.springframework.stereotype.Component;

@Component
public class IbanEncryptionAdapter implements IbanEncryptionPort {

    private final HybridEncryptionService hybridEncryptionService;

    public IbanEncryptionAdapter(HybridEncryptionService hybridEncryptionService) {
        this.hybridEncryptionService = hybridEncryptionService;
    }

    @Override
    public EncryptedIban encrypt(String plainIban) {
        return hybridEncryptionService.encryptIban(plainIban);
    }

    @Override
    public String decrypt(EncryptedIban encryptedIban) {
        return hybridEncryptionService.decryptIban(encryptedIban);
    }
}
