package com.smartwallet.infrastructure.security;

import com.smartwallet.domain.model.EncryptedIban;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;


@Component
public class HybridEncryptionService {

    private static final String AES_ALGORITHM = "AES";
    private static final String AES_TRANSFORMATION = "AES/GCM/NoPadding";
    private static final String RSA_TRANSFORMATION = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";
    private static final int GCM_IV_LENGTH_BYTES = 12;
    private static final int GCM_TAG_LENGTH_BITS = 128;
    private static final int AES_KEY_SIZE_BITS = 256;

    private final PublicKey encryptionPublicKey;
    private final PrivateKey encryptionPrivateKey;
    private final SecureRandom secureRandom = new SecureRandom();

    public HybridEncryptionService(
            ResourceLoader resourceLoader,
            @Value("${encryption.private-key-path}") String privateKeyPath,
            @Value("${encryption.public-key-path}") String publicKeyPath) throws Exception {
        this.encryptionPrivateKey = loadPrivateKey(resourceLoader, privateKeyPath);
        this.encryptionPublicKey = loadPublicKey(resourceLoader, publicKeyPath);
    }

    public EncryptedIban encryptIban(String plainIban) {
        try {
            SecretKey aesKey = generateAesKey();
            byte[] iv = new byte[GCM_IV_LENGTH_BYTES];
            secureRandom.nextBytes(iv);

            Cipher aesCipher = Cipher.getInstance(AES_TRANSFORMATION);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv);
            aesCipher.init(Cipher.ENCRYPT_MODE, aesKey, gcmSpec);
            byte[] encryptedData = aesCipher.doFinal(plainIban.getBytes(StandardCharsets.UTF_8));

            Cipher rsaCipher = Cipher.getInstance(RSA_TRANSFORMATION);
            rsaCipher.init(Cipher.ENCRYPT_MODE, encryptionPublicKey);
            byte[] encryptedDataKey = rsaCipher.doFinal(aesKey.getEncoded());

            return new EncryptedIban(encryptedData, encryptedDataKey, iv);
        } catch (Exception e) {
            throw new IllegalStateException("IBAN encryption failed", e);
        }
    }

    public String decryptIban(EncryptedIban encryptedIban) {
        try {
            Cipher rsaCipher = Cipher.getInstance(RSA_TRANSFORMATION);
            rsaCipher.init(Cipher.DECRYPT_MODE, encryptionPrivateKey);
            byte[] aesKeyBytes = rsaCipher.doFinal(encryptedIban.getEncryptedDataKey());
            SecretKey aesKey = new SecretKeySpec(aesKeyBytes, AES_ALGORITHM);

            Cipher aesCipher = Cipher.getInstance(AES_TRANSFORMATION);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH_BITS, encryptedIban.getIv());
            aesCipher.init(Cipher.DECRYPT_MODE, aesKey, gcmSpec);
            byte[] decrypted = aesCipher.doFinal(encryptedIban.getEncryptedData());

            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("IBAN decryption failed", e);
        }
    }

    private SecretKey generateAesKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(AES_ALGORITHM);
        keyGenerator.init(AES_KEY_SIZE_BITS);
        return keyGenerator.generateKey();
    }

    private static PrivateKey loadPrivateKey(ResourceLoader resourceLoader, String path) throws Exception {
        String pem = readPem(resourceLoader, path)
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
        byte[] decoded = Base64.getDecoder().decode(pem);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(decoded));
    }

    private static PublicKey loadPublicKey(ResourceLoader resourceLoader, String path) throws Exception {
        String pem = readPem(resourceLoader, path)
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
        byte[] decoded = Base64.getDecoder().decode(pem);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(new X509EncodedKeySpec(decoded));
    }

    private static String readPem(ResourceLoader resourceLoader, String path) throws IOException {
        try (InputStream is = resourceLoader.getResource(path).getInputStream()) {
            return new String(is.readAllBytes());
        }
    }
}
