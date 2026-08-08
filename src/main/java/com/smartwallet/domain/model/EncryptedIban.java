package com.smartwallet.domain.model;

import java.util.Objects;


public final class EncryptedIban {

    private final byte[] encryptedData;
    private final byte[] encryptedDataKey;
    private final byte[] iv;

    public EncryptedIban(byte[] encryptedData, byte[] encryptedDataKey, byte[] iv) {
        this.encryptedData = Objects.requireNonNull(encryptedData, "encryptedData must not be null");
        this.encryptedDataKey = Objects.requireNonNull(encryptedDataKey, "encryptedDataKey must not be null");
        this.iv = Objects.requireNonNull(iv, "iv must not be null");
    }

    public byte[] getEncryptedData() {
        return encryptedData;
    }

    public byte[] getEncryptedDataKey() {
        return encryptedDataKey;
    }

    public byte[] getIv() {
        return iv;
    }
}
