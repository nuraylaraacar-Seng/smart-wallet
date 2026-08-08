package com.smartwallet.application.port.out;

import com.smartwallet.domain.model.EncryptedIban;

public interface IbanEncryptionPort {
    EncryptedIban encrypt(String plainIban);
    String decrypt(EncryptedIban encryptedIban);
}
